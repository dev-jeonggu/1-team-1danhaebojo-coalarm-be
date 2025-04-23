package _1danhebojo.coalarm.coalarm_service.domain.coin.controller;

import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinPredictDTO;
import _1danhebojo.coalarm.coalarm_service.domain.coin.controller.response.CoinWithPriceDTO;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.controller.response.CoinDTO;
import _1danhebojo.coalarm.coalarm_service.domain.coin.service.CoinService;
import _1danhebojo.coalarm.coalarm_service.domain.user.service.AuthService;
import _1danhebojo.coalarm.coalarm_service.global.api.BaseResponse;
import _1danhebojo.coalarm.coalarm_service.global.api.OffsetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;
    private final AuthService authService;
    @GetMapping("")
    public ResponseEntity<BaseResponse<List<CoinDTO>>> getAllCoins() {
        return ResponseEntity.ok(BaseResponse.success(
                coinService.getAllCoins()
        ));
    }

    @GetMapping("/alerts/me")
    public ResponseEntity<BaseResponse<List<CoinDTO>>> getMyAlertCoins() {
        return ResponseEntity.ok(BaseResponse.success(
                coinService.getMyAlertCoins(
                        authService.getLoginUserId()
                )
        ));
    }

    @GetMapping("/paging")
    public ResponseEntity<BaseResponse<OffsetResponse<CoinDTO>>> getCoinsWithPaging(
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                coinService.getCoinsWithPaging(
                        offset,
                        limit
                )
        ));
    }

    @GetMapping("/{coinId}")
    public ResponseEntity<BaseResponse<CoinDTO>> getCoinById(
            @PathVariable("coinId") Long coinId
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                coinService.getCoinById(coinId)
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<CoinWithPriceDTO>>> searchCoins(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("quoteSymbol") String quoteSymbol
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                coinService.searchCoinWithPrice(keyword, quoteSymbol)
        ));
    }

    //코인 예측
    @GetMapping("/predict")
    public ResponseEntity<BaseResponse<CoinPredictDTO>> predictCoins(
            @RequestParam(value = "coin") String coin,
            @RequestParam("days") Integer days
    ){
        return ResponseEntity.ok(BaseResponse.success(coinService.predictCoin(coin,days)));
    }
}