package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

open class SetCandlesService(
    private val fetchProductPriceQuery: FetchProductPriceQuery,
    private val candlePort: CandlePort,
) : SetCandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        fetchInitCandles(productPriceKey)
        requestPollingCandles(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey) {
        if (candlePort.hasCandles(productPriceKey)) {
            return
        }

        fetchProductPriceQuery.fetchInitProductPrices(productPriceKey).let { initCandles ->
            candlePort.setCandles(productPriceKey, initCandles)
        }
    }

    private suspend fun requestPollingCandles(productPriceKey: ProductPriceKey) {
        fetchProductPriceQuery.requestPollingProductPrice(productPriceKey)
    }
}