package _1danhebojo.coalarm.coalarm_service.domain.coin.service;

import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinPredictDTO;
import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinWithPriceDTO;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.controller.response.CoinDTO;
import _1danhebojo.coalarm.coalarm_service.global.api.OffsetResponse;

import java.util.List;

public interface CoinService {
    List<CoinDTO> getAllCoins();
    List<CoinDTO> getMyAlertCoins(Long userId);
    OffsetResponse<CoinDTO> getCoinsWithPaging(Integer offset, Integer limit);
    CoinDTO getCoinById(Long coinId);
    List<CoinDTO> searchCoinByNameOrSymbol(String term);
    List<CoinWithPriceDTO> searchCoinWithPrice(String keyword, String quoteSymbol);

    CoinPredictDTO predictCoin(String coin, Integer days);
}