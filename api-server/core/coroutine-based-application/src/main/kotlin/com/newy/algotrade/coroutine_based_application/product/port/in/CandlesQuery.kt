package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product.ProductPriceKey

interface CandlesQuery :
    GetCandlesQuery

fun interface GetCandlesQuery {
    fun getCandles(key: ProductPriceKey): Candles
}