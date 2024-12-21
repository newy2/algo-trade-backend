package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.get
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam
import com.newy.algotrade.product_price.domain.jackson.ByBitProductPriceHttpResponse
import com.newy.algotrade.product_price.port.out.ProductPricePort

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
