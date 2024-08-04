package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetCandlesQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.GetCandlesPort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product.ProductPriceKey

open class GetCandlesQueryService(
    private val getCandlesPort: GetCandlesPort
) : GetCandlesQuery {
    constructor(candlePort: CandlePort) : this(
        getCandlesPort = candlePort
    )

    override fun getCandles(key: ProductPriceKey): Candles =
        getCandlesPort.getCandles(key)
}