package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.price.ProductPriceKey

open class CandlesQueryService(
    private val candleQueryPort: CandleQueryPort
) : CandlesQuery {
    override fun getCandles(key: ProductPriceKey): Candles =
        candleQueryPort.getCandles(key)
}