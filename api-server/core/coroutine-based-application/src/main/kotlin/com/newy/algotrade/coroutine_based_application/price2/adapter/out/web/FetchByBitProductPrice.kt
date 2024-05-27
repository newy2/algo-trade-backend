package com.newy.algotrade.coroutine_based_application.price2.adapter.out.web

import com.newy.algotrade.coroutine_based_application.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.get
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceHttpResponse

class FetchByBitProductPrice(private val client: HttpApiClient) : GetProductPricePort {
    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        val (path, apiRateLimit) = ByBitHttpApiInfo.loadProductPrice()
        apiRateLimit.await()

        val response = client.get<ByBitProductPriceHttpResponse>(
            path = path,
            params = mapOf(
                "category" to param.category(),
                "symbol" to param.productCode,
                "interval" to param.formattedInterval(),
                "end" to param.endTime(),
                "limit" to param.limit.toString(),
            ),
            jsonExtraValues = ByBitProductPriceHttpResponse.jsonExtraValues(param.intervalMinutes),
        )

        return response.prices.sortedBy { it.time.begin }
    }
}
