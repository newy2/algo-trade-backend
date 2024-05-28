package com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle

import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteCandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.HasStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.UnSubscribePollingProductPricePort
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RemoveCandlesUseCase(
    private val strategyPort: HasStrategyPort,
    private val candlePort: DeleteCandlePort,
    private val pollingProductPricePort: UnSubscribePollingProductPricePort,
) {
    fun removeCandles(productPriceKey: ProductPriceKey) {
        if (strategyPort.hasProductPriceKey(productPriceKey)) {
            return
        }

        candlePort.deleteCandles(productPriceKey)
        pollingProductPricePort.unSubscribe(productPriceKey)
    }
}
