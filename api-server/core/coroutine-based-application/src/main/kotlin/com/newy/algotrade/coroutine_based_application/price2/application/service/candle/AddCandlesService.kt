package com.newy.algotrade.coroutine_based_application.price2.application.service.candle

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.AddCandlePort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class AddCandlesService(
    private val candlePort: AddCandlePort
) : AddCandlesUseCase {
    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles {
        return candlePort.addCandles(productPriceKey, candleList)
    }
}