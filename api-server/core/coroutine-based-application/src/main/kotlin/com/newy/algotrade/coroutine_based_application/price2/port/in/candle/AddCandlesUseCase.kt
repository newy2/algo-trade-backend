package com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface AddCandlesUseCase {
    fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles
}