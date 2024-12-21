package com.newy.algotrade.product_price.port.out

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey

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