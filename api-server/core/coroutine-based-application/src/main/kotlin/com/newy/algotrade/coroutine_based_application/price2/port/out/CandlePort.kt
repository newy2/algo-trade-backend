package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface CandlePort : DeleteCandlePort, GetCandlePort {
    fun setCandles(key: ProductPriceKey, list: List<Candle>): Candles
    fun addCandles(key: ProductPriceKey, list: List<Candle>): Candles
}

interface GetCandlePort {
    fun getCandles(key: ProductPriceKey): Candles
}

interface DeleteCandlePort {
    fun deleteCandles(key: ProductPriceKey)
}