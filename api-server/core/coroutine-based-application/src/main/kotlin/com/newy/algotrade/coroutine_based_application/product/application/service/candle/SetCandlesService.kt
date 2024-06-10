package com.newy.algotrade.coroutine_based_application.product.application.service.candle

import com.newy.algotrade.coroutine_based_application.product.port.`in`.candle.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

class SetCandlesService(
    private val productPricePort: GetProductPricePort,
    private val pollingProductPricePort: SubscribePollingProductPricePort,
    private val candlePort: CandlePort,
    private val initDataSize: Int = DEFAULT_CANDLE_SIZE
) : SetCandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        fetchInitCandles(productPriceKey)
        pollingCandles(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey) {
        if (candlePort.getCandles(productPriceKey).size != 0) {
            return
        }

        productPricePort.getProductPrices(
            GetProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        ).let {
            candlePort.setCandles(productPriceKey, it)
        }
    }

    private suspend fun pollingCandles(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.subscribe(productPriceKey)
    }
}
