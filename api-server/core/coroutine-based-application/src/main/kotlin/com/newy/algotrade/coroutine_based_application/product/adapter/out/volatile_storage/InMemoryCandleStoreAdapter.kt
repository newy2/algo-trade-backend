package com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.product.ProductPriceKey

open class InMemoryCandleStoreAdapter : CandlePort {
    private val candleMap = mutableMapOf<ProductPriceKey, Candles>()

    override fun getCandles(key: ProductPriceKey): Candles =
        candleMap[key] ?: DEFAULT_CHART_FACTORY.candles()

    override fun hasCandles(key: ProductPriceKey): Boolean =
        getCandles(key).size > 0

    override fun setCandles(key: ProductPriceKey, list: List<Candle>): Candles =
        DEFAULT_CHART_FACTORY.candles().also {
            it.upsert(list)
            candleMap[key] = it
        }

    override fun addCandles(key: ProductPriceKey, list: List<Candle>): Candles =
        getCandles(key).also {
            // TODO service 로 로직 옮겨야 함
            if (it.size == 0) {
                return it
            }

            it.upsert(list)
        }

    override fun removeCandles(key: ProductPriceKey) {
        candleMap.remove(key)
    }
}