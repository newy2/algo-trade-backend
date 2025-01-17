package com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.get
import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.domain.product_price.jackson.ByBitProductPriceHttpResponse

class FetchByBitProductPrice(private val client: HttpApiClient) : ProductPricePort {
    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
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
