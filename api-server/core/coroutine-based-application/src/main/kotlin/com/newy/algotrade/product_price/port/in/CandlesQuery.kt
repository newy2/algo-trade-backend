package com.newy.algotrade.product_price.port.`in`

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product_price.ProductPriceKey

interface CandlesQuery :
    GetCandlesQuery

fun interface GetCandlesQuery {
    fun getCandles(key: ProductPriceKey): Candles
}