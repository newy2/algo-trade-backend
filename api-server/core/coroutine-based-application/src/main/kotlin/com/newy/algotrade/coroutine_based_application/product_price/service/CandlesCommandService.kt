package com.newy.algotrade.coroutine_based_application.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.ProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlesPort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

open class CandlesCommandService(
    private val productPriceQuery: ProductPriceQuery,
    private val candlesPort: CandlesPort,
) : CandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey): Candles =
        fetchInitCandles(productPriceKey).also {
            requestPollingCandles(productPriceKey)
        }

    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles =
        candlesPort.saveWithAppendCandles(productPriceKey, candleList)

    override fun removeCandles(productPriceKey: ProductPriceKey) {
        candlesPort.deleteCandles(productPriceKey)
        productPriceQuery.requestUnPollingProductPrice(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey): Candles =
        candlesPort.findCandles(productPriceKey).takeIf { it.size > 0 }
            ?: productPriceQuery.getInitProductPrices(productPriceKey).let { initCandles ->
                candlesPort.saveWithReplaceCandles(productPriceKey, initCandles)
            }

    private fun requestPollingCandles(productPriceKey: ProductPriceKey) {
        productPriceQuery.requestPollingProductPrice(productPriceKey)
    }
}