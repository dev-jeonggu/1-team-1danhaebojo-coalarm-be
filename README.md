# 코알람 (Coalarm)
코알람은 실시간으로 코인 시장의 데이터를 확인하고 사용자 맞춤형 알림을 제공하며, 가격 예측을 통해 투자 판단을 돕는 암호 화폐 투자 보조 서비스입니다.

<br />

## 👩🏻‍💻 Developer
| sando.kang (강산아) | yuni.jeong (정해윤) | mason.park (박민수) | jeonggu.kim (김정현) | leeroy.kim (김동현) |
|:---:|:---:|:---:|:---:|:---:|
|  <a href="https://github.com/gsandoo"> <img src="https://avatars.githubusercontent.com/gsandoo" width=100px alt="_"/> </a> | <a href="https://github.com/hyoon1129"> <img src="https://avatars.githubusercontent.com/hyoon1129" width=100px alt="_"/> </a> | <a href="https://github.com/pmsu2007"> <img src="https://avatars.githubusercontent.com/pmsu2007" width=100px alt="_"/> </a> | <a href="https://github.com/dev-jeonggu"> <img src="https://avatars.githubusercontent.com/dev-jeonggu" width=100px alt="_"/> </a> | <a href="https://github.com/Dwisgolmog"> <img src="https://avatars.githubusercontent.com/Dwisgolmog" width=100px alt="_"/> </a> |
| <a href="https://github.com/gsandoo">@gsandoo</a> | <a href="https://github.com/hyoon1129">@hyoon1129</a> | <a href="https://github.com/pmsu2007">@pmsu2007</a> | <a href="https://github.com/dev-jeonggu">@dev-jeonggu</a> | <a href="https://github.com/Dwisgolmog">@Dwisgolmog</a> |

<br />

## 🛠️ Stack
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white) 
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white) 
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white) 
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white) 
![TimescaleDB](https://img.shields.io/badge/TimescaleDB-FD7E14?style=flat&logo=timescale&logoColor=white)

<br />

## ✨ Main Feature

### 1. 실시간 코인 알림 기능
    • 지정가 도달, 거래량 급등, 골든크로스 발생 시 실시간으로 알림을 받을 수 있습니다.
    • 업비트 WebSocket API와 Spring Boot SSE(Server-Sent Events)를 활용하여 푸시 알림을 제공합니다.

### 2. 김치 프리미엄 추적
    • 국내(업비트)와 해외(바이낸스) 거래소 간의 가격 차이를 실시간으로 추적할 수 있습니다.
    • 프리미엄 상승/하락 구간에 따라 조건부 알람을 설정할 수 있습니다.

### 3. 실시간 체결 데이터 기반 분석
    • 실시간 체결 데이터를 수집해 이상 거래 징후나 변동성을 탐지할 수 있습니다.
    • 프론트엔드에서 체결량 및 호가 변화를 시각화하여 직관적인 분석이 가능합니다.
  
### 4. 유사 패턴(기영이 패턴) 감지 기능
    • 과거 특정 차트 패턴(기영이)과 유사한 움직임을 보이는 종목을 자동 탐지합니다.
    • 유사도 기반 알림을 설정하여 관심 종목을 미리 포착할 수 있습니다.

### 5. 예측 기반 코인 가격 안내
    • 선택한 코인의 가격을 1분/1시간/1일 등 n일 미래 시점 기준으로 예측할 수 있습니다.
    • 딥러닐 모델을 사용하여 실시간 예측 결과를 제공합니다.

<br />

## 💻 Screen Preview
<img width="1075" alt="coalarm-screen" src="https://github.com/user-attachments/assets/e38e500d-08b3-4d3c-8995-8860485f6f7d" />

<br />

## 💻 Screen Architecture
![코알람 drawio](https://github.com/user-attachments/assets/54e71873-eac2-4468-b42a-e21e3b668cb9)


