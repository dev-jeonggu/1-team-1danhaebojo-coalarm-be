package _1danhebojo.coalarm.coalarm_service.domain.coin.repository;

import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinWithPriceDTO;
import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;

import java.util.List;

public interface CoinRepository {
    List<CoinEntity> findAlertCoinsByUserId(Long userId);
    List<CoinWithPriceDTO> searchCoinsWithLatestPrice(String keyword, String quoteSymbol);

    CoinEntity findByName(String coin);
    CoinEntity findBySymbol(String symbol);
}
