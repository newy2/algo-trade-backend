package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface CandlePort : RemoveCandlePort, GetCandlePort, AddCandlePort {
    fun setCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}

interface AddCandlePort {
    fun addCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}

interface GetCandlePort {
    fun getCandles(key: ProductPriceKey): Candles
}

interface RemoveCandlePort {
    fun removeCandles(key: ProductPriceKey)
}