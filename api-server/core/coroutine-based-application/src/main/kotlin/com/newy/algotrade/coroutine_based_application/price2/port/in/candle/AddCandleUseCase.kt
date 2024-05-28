package com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface AddCandleUseCase {
    fun addCandle(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles
}