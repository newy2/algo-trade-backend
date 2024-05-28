package com.newy.algotrade.coroutine_based_application.price2.application.service.candle

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.AddCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.AddCandlePort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class AddCandleService(
    private val candlePort: AddCandlePort
) : AddCandleUseCase {
    override fun addCandle(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles {
        return candlePort.addCandles(productPriceKey, candleList)
    }
}