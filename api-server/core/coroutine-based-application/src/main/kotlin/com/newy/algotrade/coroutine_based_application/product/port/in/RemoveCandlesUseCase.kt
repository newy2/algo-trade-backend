package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleCommandPort
import com.newy.algotrade.coroutine_based_application.product.port.out.UnSubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyQueryPort
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RemoveCandlesUseCase(
    private val strategyPort: StrategyQueryPort,
    private val candlePort: CandleCommandPort,
    private val pollingProductPricePort: UnSubscribePollingProductPricePort,
) {
    fun removeCandles(productPriceKey: ProductPriceKey) {
        if (strategyPort.hasProductPriceKey(productPriceKey)) {
            return
        }

        candlePort.removeCandles(productPriceKey)
        pollingProductPricePort.unSubscribe(productPriceKey)
    }
}
