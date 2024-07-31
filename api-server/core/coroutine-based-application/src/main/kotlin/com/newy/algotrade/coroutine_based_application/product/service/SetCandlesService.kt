package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

open class SetCandlesService(
    private val productPricePort: ProductPriceQueryPort,
    private val pollingProductPricePort: SubscribePollingProductPricePort,
    private val candlePort: CandlePort,
    private val initDataSize: Int = DEFAULT_CANDLE_SIZE
) : SetCandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        fetchInitCandles(productPriceKey)
        pollingCandles(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey) {
        if (candlePort.hasCandles(productPriceKey)) {
            return
        }

        productPricePort.getProductPrices(
            GetProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        ).let { initCandles ->
            candlePort.setCandles(productPriceKey, initCandles)
        }
    }

    private suspend fun pollingCandles(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.subscribe(productPriceKey)
    }
}
