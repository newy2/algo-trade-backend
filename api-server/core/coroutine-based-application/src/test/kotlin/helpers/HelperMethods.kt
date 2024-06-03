package helpers

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.Duration

fun productPriceKey(productCode: String, interval: Duration = Duration.ofMinutes(1)) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)

fun userStrategyKey(userStrategyId: String, productPriceKey: ProductPriceKey) =
    UserStrategyKey(
        userStrategyId,
        StrategyId.BuyTripleRSIStrategy,
        productPriceKey
    )