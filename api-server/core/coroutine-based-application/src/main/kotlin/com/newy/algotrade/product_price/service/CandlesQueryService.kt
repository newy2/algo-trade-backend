package com.newy.algotrade.product_price.service

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.`in`.CandlesQuery
import com.newy.algotrade.product_price.port.out.CandlesPort
import com.newy.algotrade.product_price.port.out.FindCandlesPort

open class CandlesQueryService(
    private val findCandlesPort: FindCandlesPort
) : CandlesQuery {
    constructor(candlesPort: CandlesPort) : this(
        findCandlesPort = candlesPort
    )

    override fun getCandles(key: ProductPriceKey): Candles =
        findCandlesPort.findCandles(key)
}