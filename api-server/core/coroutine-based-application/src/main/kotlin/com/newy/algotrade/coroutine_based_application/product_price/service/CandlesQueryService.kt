package com.newy.algotrade.coroutine_based_application.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlesPort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.FindCandlesPort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product_price.ProductPriceKey

open class CandlesQueryService(
    private val findCandlesPort: FindCandlesPort
) : CandlesQuery {
    constructor(candlesPort: CandlesPort) : this(
        findCandlesPort = candlesPort
    )

    override fun getCandles(key: ProductPriceKey): Candles =
        findCandlesPort.findCandles(key)
}