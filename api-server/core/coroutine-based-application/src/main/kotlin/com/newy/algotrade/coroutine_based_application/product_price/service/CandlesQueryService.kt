package com.newy.algotrade.coroutine_based_application.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.GetCandlesPort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product_price.ProductPriceKey

open class CandlesQueryService(
    private val getCandlesPort: GetCandlesPort
) : CandlesQuery {
    constructor(candlePort: CandlePort) : this(
        getCandlesPort = candlePort
    )

    override fun getCandles(key: ProductPriceKey): Candles =
        getCandlesPort.getCandles(key)
}