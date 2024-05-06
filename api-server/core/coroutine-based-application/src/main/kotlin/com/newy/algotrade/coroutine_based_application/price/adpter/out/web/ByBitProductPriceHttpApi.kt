package com.newy.algotrade.coroutine_based_application.price.adpter.out.web

import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.coroutine_based_application.price.port.out.GetProductPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceHttpResponse
import java.time.Duration
import java.time.OffsetDateTime

class ByBitProductPriceHttpApi(client: HttpApiClient) : GetProductPricePort(client) {
    override suspend fun productPrices(
        category: String,
        symbol: String,
        interval: Duration,
        endTime: OffsetDateTime,
        limit: Int
    ): List<ProductPrice> {
        val minuteInterval = interval.toMinutes()

        val response = client.get<ByBitProductPriceHttpResponse>(
            path = "/v5/market/kline",
            params = mapOf(
                "category" to category,
                "symbol" to symbol,
                "interval" to minuteInterval.toString(),
                "end" to endTime.toInstant().toEpochMilli().toString(),
                "limit" to limit.toString(),
            ),
            jsonExtraValues = ByBitProductPriceHttpResponse.jsonExtraValues(minuteInterval),
        )

        return response.prices
    }
}
