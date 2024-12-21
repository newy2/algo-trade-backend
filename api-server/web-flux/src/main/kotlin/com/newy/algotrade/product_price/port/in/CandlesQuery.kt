package com.newy.algotrade.product_price.port.`in`

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.product_price.domain.ProductPriceKey

interface CandlesQuery :
    GetCandlesQuery

fun interface GetCandlesQuery {
    fun getCandles(key: ProductPriceKey): Candles
}