package com.newy.algotrade.coroutine_based_application.product_price.port.out

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

interface CandlesPort :
    FindCandlesPort,
    ExistsCandlesPort,
    SaveWithAppendCandlesPort,
    DeleteCandlesPort,
    SaveWithReplaceCandlesPort

fun interface FindCandlesPort {
    fun findCandles(key: ProductPriceKey): Candles
}

fun interface ExistsCandlesPort {
    fun existsCandles(key: ProductPriceKey): Boolean
}

fun interface SaveWithAppendCandlesPort {
    fun saveWithAppendCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}

fun interface DeleteCandlesPort {
    fun deleteCandles(key: ProductPriceKey)
}

fun interface SaveWithReplaceCandlesPort {
    fun saveWithReplaceCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles
}