package com.newy.algotrade.product_price.adapter.out.volatile_storage

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.CandlesPort

open class InMemoryCandlesStoreAdapter : CandlesPort {
    private val candleMap = mutableMapOf<ProductPriceKey, Candles>()

    override fun findCandles(key: ProductPriceKey): Candles =
        candleMap[key] ?: DEFAULT_CHART_FACTORY.candles()

    override fun existsCandles(key: ProductPriceKey): Boolean =
        findCandles(key).size > 0

    override fun saveWithReplaceCandles(key: ProductPriceKey, list: List<Candle>): Candles =
        DEFAULT_CHART_FACTORY.candles().also {
            it.upsert(list)
            candleMap[key] = it
        }

    override fun saveWithAppendCandles(key: ProductPriceKey, list: List<Candle>): Candles =
        findCandles(key).also {
            // TODO service 로 로직 옮겨야 함
            if (it.size == 0) {
                return it
            }

            it.upsert(list)
        }

    override fun deleteCandles(key: ProductPriceKey) {
        candleMap.remove(key)
    }
}