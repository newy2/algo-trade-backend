package com.newy.algotrade.coroutine_based_application.price.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import java.time.Duration
import java.time.OffsetDateTime

interface GetProductPricePort {
    suspend fun productPrices(
        market: String,
        category: String,
        symbol: String,
        interval: Duration,
        endTime: OffsetDateTime,
        limit: Int
    ): List<ProductPrice>
}