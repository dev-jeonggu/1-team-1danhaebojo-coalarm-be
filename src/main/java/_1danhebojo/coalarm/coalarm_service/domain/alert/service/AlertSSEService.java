package _1danhebojo.coalarm.coalarm_service.domain.alert.service;

import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.AlertHistoryRepositoryImpl;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.AlertRepositoryImpl;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.Alert;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertSSEService {
    private final AlertHistoryService alertHistoryService;
    private final AlertRepositoryImpl alertRepositoryImpl;
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final Map<Long, List<Alert>> activeAlertList = new ConcurrentHashMap<>();
    private final DiscordService discordService;

    @Lazy
    @Autowired
    private GoldCrossAndTargetPriceService goldCrossAndTargetPriceService;

    @PostConstruct
    public void init() {
        getActiveAlertsGroupedByUser();
    }

    // 전체 활성화된 사용자의 알람 저장
    public void getActiveAlertsGroupedByUser() {
        List<Alert> activeAlerts = alertRepositoryImpl.findAllActiveAlerts();

        // userId를 key로, List<Alert>을 value로 하는 Map 생성
        activeAlertList.clear(); // 기존 데이터 삭제
        activeAlertList.putAll(
                activeAlerts.stream()
                        .collect(Collectors.groupingBy(alert -> alert.getUser().getUserId()))
        );
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void discordScheduler() {
        Map<Long, List<Alert>> filteredAlerts = activeAlertList.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // 사용자 ID 유지 (key)
                        entry -> entry.getValue().stream() // value(알람 리스트) 필터링
                                .filter(alert -> alert.isTargetPriceFlag() || alert.isGoldenCrossFlag())
                                .collect(Collectors.toList())
                ));
        filteredAlerts.forEach(this::sendAlertListToUserDiscord);
    }

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void checkAlertsForSubscribedUsers() {
        for (Long userId : userEmitters.keySet()) {
            List<Alert> activeAlerts = new ArrayList<>(activeAlertList.getOrDefault(userId, Collections.emptyList()));

            // 유효성 추가
            if (activeAlerts == null || activeAlerts.isEmpty()) continue;

            // 활성화된 알람 SSE로 보내기
            for (Alert alert : activeAlerts) {
                if (goldCrossAndTargetPriceService.isPriceReached(alert) && goldCrossAndTargetPriceService.isPriceStillValid(alert)) {
                    sendAlertToUserSSE(userId, alert);
                }
            }
        }
    }

    // 로그인한 사용자가 실행
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(0L);

        userEmitters.computeIfAbsent(userId, k -> new ArrayList<>()).add(emitter);

        // 사용자의 기존 알람을 전송
        sendUserAlerts(userId, emitter);

        // 연결 종료 시 emitter 제거
        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError((e) -> removeEmitter(userId));

        return emitter;
    }

    // 사용자의 기존 알람을 새로운 Emitter에게 전송
    public void sendUserAlerts(Long userId, SseEmitter emitter) {
        // TargetPrice랑 GoldenCross만 가져오기
        List<Alert> alerts = Optional.ofNullable(activeAlertList.get(userId))
                .orElse(Collections.emptyList()) // null이면 빈 리스트 반환
                .stream()
                .filter(alert -> alert.isTargetPriceFlag() || alert.isGoldenCrossFlag())
                .collect(Collectors.toList());

        try {
            if(alerts != null) {
                emitter.send(SseEmitter.event()
                        .name("existing-alerts") // 기존 알람 목록
                        .data(alerts) // 기존 알람 데이터를 리스트로 전송
                );

                for (Alert alert : alerts) {
                    alertHistoryService.addAlertHistory(alert.getAlertId(), userId); // 🔥 기존 Alert ID 활용
                }
            }
        } catch (IOException e) {
            removeEmitter(userId);
        }
    }

    // 사용자의 기존 알람 SSE 전송
    public void sendAlertToUserSSE(Long userId, Alert alert) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            log.info("사용자 " + userId + " 에 대한 SSE 연결이 없습니다.");
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("alert").data(alert));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);

        // 더 이상 연결이 없는 유저에 대해서는 Map에서 아예 지워버리고, 연결이 남아 있는 경우만 최신 상태로 다시 저장한다."
        if (emitters.isEmpty()) {
            userEmitters.remove(userId);
        }

        // 알람 히스토리 저장
        alertHistoryService.addAlertHistory(alert.getAlertId(), Long.valueOf(userId));
    }

    // 사용자의 기존 알람 discord 전송
    public void sendAlertToUserDiscord(Long userId, Alert alert) {
        // 최종 메시지 생성
        if(alert == null) {
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("👤 사용자 닉네임: ").append(alert.getUser().getNickname()).append("\n");
        messageBuilder.append("📢(코인) ").append(alert.getCoin().getName());
        messageBuilder.append(", (제목) " + alert.getTitle()).append("\n");
        if (alert.getUser().getDiscordWebhook() != null && !alert.getUser().getDiscordWebhook().isEmpty()) {
            discordService.sendDiscordAlert(alert.getUser().getDiscordWebhook(), messageBuilder.toString());
        }
    }

    // 사용자의 알람 스케줄러 discord 전송
    public void sendAlertListToUserDiscord(Long userId, List<Alert> alerts) {
        StringBuilder messageBuilder = new StringBuilder();

        if (alerts.isEmpty()) {
            return;
        }
        if (alerts.get(0).getUser() == null) {
            return;
        }
        String nickname = alerts.get(0).getUser().getNickname();
        messageBuilder.append("👤 사용자 닉네임: ").append(alerts.get(0).getUser().getNickname()).append("\n");

        alerts.forEach(alert -> {
            messageBuilder.append("📢(코인) ").append(alert.getCoin().getSymbol())
                    .append(", (제목) ").append(alert.getTitle()).append("\n");
        });

        String message = messageBuilder.toString();

        // 디스코드로 메시지 전송
        if (alerts.get(0).getUser().getDiscordWebhook() != null && !alerts.get(0).getUser().getDiscordWebhook().isEmpty()) {
            discordService.sendDiscordAlert(alerts.get(0).getUser().getDiscordWebhook(), message);
        }
    }

    // 새로운 알람 추가
    // 알림을 추가했을 때 SseEmitter에 추가하는 부분이 필요
    public void addEmitter(Long userId, Alert alert) {
        SseEmitter emitter = new SseEmitter(0L);

        // 내부 동작
        userEmitters.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>())).add(emitter);
        activeAlertList.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>())).add(alert);

        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError((e) -> removeEmitter(userId));

        log.info("📢 사용자 " + userId + " 에 대한 새로운 SSE 구독 추가됨. 활성화된 알람 개수: " + activeAlertList.get(userId).size());
    }

    // SSE 알람 제거
    public void deleteEmitter(Long userId, Alert alert) {
        // 사용자의 알람 리스트에서 해당 알람 제거
        activeAlertList.computeIfPresent(userId, (k, alerts) -> {
            alerts.removeIf(a -> a.getAlertId().equals(alert.getAlertId())); // ✅ alertId가 동일한 경우만 삭제
            return alerts.isEmpty() ? null : alerts; // 리스트가 비면 null 반환해서 Map에서 삭제
        });

        log.info("사용자 " + userId + " 의 알람 제거됨. 남은 알람 개수: "
                + (activeAlertList.containsKey(userId) ? activeAlertList.get(userId).size() : 0));
    }

    // SSE 구독 취소
    public void removeEmitter(Long userId) {
        List<SseEmitter> emitters = userEmitters.remove(userId); // 해당 userId의 모든 SSE 제거
        activeAlertList.remove(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.complete(); // 안전하게 종료
                } catch (Exception e) {
                    log.warn("emitter 종료 중 예외 발생: {}", e.getMessage());
                }
            }
        }
        log.info("사용자 " + userId + " 의 모든 SSE 구독 취소 완료");
    }
}
