package helpers

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.Duration
import java.time.OffsetDateTime

fun productPriceKey(productCode: String, interval: Duration = Duration.ofMinutes(1)) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.LS_SEC, ProductType.SPOT, productCode, interval)

fun userStrategyKey(userStrategyId: String, productPriceKey: ProductPriceKey) =
    UserStrategyKey(
        userStrategyId,
        StrategyId.BuyTripleRSIStrategy,
        productPriceKey
    )

fun productPrice(
    amount: Int,
    interval: Duration,
    beginTime: OffsetDateTime = OffsetDateTime.parse("2024-05-01T00:00Z")
) =
    Candle.TimeFrame.from(interval)!!(
        beginTime,
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        0.toBigDecimal(),
    )