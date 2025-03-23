package _1danhebojo.coalarm.coalarm_service.domain.dashboard.service;

import _1danhebojo.coalarm.coalarm_service.domain.dashboard.controller.response.CoinDTO;
import _1danhebojo.coalarm.coalarm_service.global.api.OffsetResponse;

import java.util.List;

public interface CoinService {
    List<CoinDTO> getAllCoins();
    OffsetResponse<CoinDTO> getCoinsWithPaging(Integer offset, Integer limit);
    CoinDTO getCoinById(Long coinId);
}