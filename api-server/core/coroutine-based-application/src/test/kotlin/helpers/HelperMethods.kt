package helpers

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.user_strategy.domain.UserStrategyKey
import java.time.Duration
import java.time.OffsetDateTime

fun productPriceKey(
    productCode: String,
    interval: Duration = Duration.ofMinutes(1),
    productType: ProductType = ProductType.SPOT,
) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, productType, productCode, interval)
    else
        ProductPriceKey(Market.LS_SEC, productType, productCode, interval)

fun userStrategyKey(userStrategyId: Long, productPriceKey: ProductPriceKey) =
    UserStrategyKey(
        userStrategyId,
        "BuyTripleRSIStrategy",
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

fun getSystemProperty(name: String): String =
    System.getProperty(name, System.getenv(name)) ?: ""