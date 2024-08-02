package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

open class CandlesCommandService(
    private val fetchProductPriceQuery: FetchProductPriceQuery,
    private val candlePort: CandlePort,
) : CandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey): Candles =
        fetchInitCandles(productPriceKey).also {
            requestPollingCandles(productPriceKey)
        }

    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles =
        candlePort.addCandles(productPriceKey, candleList)

    override fun removeCandles(productPriceKey: ProductPriceKey) {
        candlePort.removeCandles(productPriceKey)
        fetchProductPriceQuery.requestUnPollingProductPrice(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey): Candles =
        candlePort.getCandles(productPriceKey).takeIf { it.size > 0 }
            ?: fetchProductPriceQuery.fetchInitProductPrices(productPriceKey).let { initCandles ->
                candlePort.setCandles(productPriceKey, initCandles)
            }

    private fun requestPollingCandles(productPriceKey: ProductPriceKey) {
        fetchProductPriceQuery.requestPollingProductPrice(productPriceKey)
    }
}