package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface CandlePort {
    fun getCandles(key: ProductPriceKey): Candles
    fun setCandles(key: ProductPriceKey, list: List<Candle>): Candles
    fun addCandles(key: ProductPriceKey, list: List<Candle>): Candles
}