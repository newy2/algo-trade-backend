package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey

interface CandlePort :
    GetCandlesPort,
    HasCandlesPort,
    AddCandlesPort,
    RemoveCandlesPort,
    SetCandlesPort

fun interface GetCandlesPort {
    fun getCandles(key: ProductPriceKey): Candles
}

fun interface HasCandlesPort {
    fun hasCandles(key: ProductPriceKey): Boolean
}

fun interface AddCandlesPort {
    fun addCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}

fun interface RemoveCandlesPort {
    fun removeCandles(key: ProductPriceKey)
}

fun interface SetCandlesPort {
    fun setCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}