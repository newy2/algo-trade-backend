package com.newy.algotrade.product_price.service

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.`in`.CandlesUseCase
import com.newy.algotrade.product_price.port.`in`.ProductPriceQuery
import com.newy.algotrade.product_price.port.out.CandlesPort

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