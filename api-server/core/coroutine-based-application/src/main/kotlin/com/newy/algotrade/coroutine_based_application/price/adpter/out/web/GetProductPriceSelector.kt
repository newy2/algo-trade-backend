package com.newy.algotrade.coroutine_based_application.price.adpter.out.web

import com.newy.algotrade.coroutine_based_application.price.port.out.GetProductPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import java.time.Duration
import java.time.OffsetDateTime

class GetProductPriceSelector(
    private val components: Map<String, GetProductPricePort>
) : GetProductPricePort {
    override suspend fun productPrices(
        market: String,
        category: String,
        symbol: String,
        interval: Duration,
        endTime: OffsetDateTime,
        limit: Int,
    ): List<ProductPrice> {
        return components.getValue(market).productPrices(market, category, symbol, interval, endTime, limit)
    }
}