package _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CoinPredictDTO {
   private final String coin;
   private final Integer days;
   private final BigDecimal price;
   private final Integer kiyoung;
}
