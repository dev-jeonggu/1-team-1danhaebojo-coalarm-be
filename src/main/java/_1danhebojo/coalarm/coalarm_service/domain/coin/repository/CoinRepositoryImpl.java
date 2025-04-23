package _1danhebojo.coalarm.coalarm_service.domain.coin.repository;

import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinWithPriceDTO;
import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.QTickerEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.QAlertEntity.alertEntity;
import static _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.QCoinEntity.coinEntity;
import static _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.QTickerEntity.tickerEntity;

@Repository
@RequiredArgsConstructor
public class CoinRepositoryImpl implements CoinRepository {

    private final JPAQueryFactory query;

    @Override
    public List<CoinEntity> findAlertCoinsByUserId(Long userId) {
        return query.selectDistinct(coinEntity)
                .from(alertEntity)
                .join(coinEntity).on(coinEntity.id.eq(alertEntity.coin.id))
                .where(alertEntity.user.id.eq(userId))
                .orderBy(coinEntity.id.asc())
                .fetch();
    }

    @Override
    public List<CoinWithPriceDTO> searchCoinsWithLatestPrice(String keyword, String quoteSymbol) {
        QTickerEntity subTickerEntity = new QTickerEntity("subTickerEntity");

        return query
                .select(Projections.constructor(
                        CoinWithPriceDTO.class,
                        coinEntity.id,
                        coinEntity.name,
                        coinEntity.symbol,
                        tickerEntity.last,
                        tickerEntity.id.timestamp
                ))
                .from(coinEntity)
                .leftJoin(tickerEntity).on(
                        tickerEntity.id.baseSymbol.eq(coinEntity.symbol),
                        tickerEntity.id.quoteSymbol.eq(quoteSymbol),
                        tickerEntity.id.timestamp.eq(
                                JPAExpressions
                                        .select(subTickerEntity.id.timestamp.max())
                                        .from(subTickerEntity)
                                        .where(
                                                subTickerEntity.id.baseSymbol.eq(coinEntity.symbol),
                                                subTickerEntity.id.quoteSymbol.eq(quoteSymbol)
                                        )
                        )
                )
                .where(
                        tickerEntity.id.quoteSymbol.eq(quoteSymbol),
                        keywordContains(keyword)
                )
                .fetch();
    }

    @Override
    public CoinEntity findByName(String coin) {
        return query.select(coinEntity).from(coinEntity).where(coinEntity.name.eq(coin)).fetchOne();
    }

    @Override
    public CoinEntity findBySymbol(String symbol) {
        return query.select(coinEntity).from(coinEntity).where(coinEntity.symbol.eq(symbol)).fetchOne();
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return coinEntity.name.containsIgnoreCase(keyword)
                .or(coinEntity.symbol.containsIgnoreCase(keyword));
    }

}
