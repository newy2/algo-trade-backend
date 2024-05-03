package com.newy.algotrade.coroutine_based_application.price

import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.domain.price.model.ByBitProductPriceHttpResponse
import com.newy.algotrade.domain.price.model.ProductPrice
import java.time.Duration
import java.time.ZonedDateTime

class ByBitProductPriceHttpApi(private val client: HttpApiClient) {
    suspend fun productPrices(
        category: String,
        symbol: String,
        interval: Duration,
        startTime: ZonedDateTime,
        limit: Int
    ): List<ProductPrice> {
        val minuteInterval = interval.toMinutes().toString()

        val response = client.get<ByBitProductPriceHttpResponse>(
            path = "/v5/market/kline",
            params = mapOf(
                "category" to category,
                "symbol" to symbol,
                "interval" to minuteInterval,
                "start" to startTime.toInstant().toEpochMilli().toString(),
                "limit" to limit.toString(),
            ),
            jsonExtraValues = ByBitProductPriceHttpResponse.jsonExtraValues(minuteInterval),
        )

        return response.prices
    }
}
