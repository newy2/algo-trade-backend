package com.newy.algotrade.coroutine_based_application.price.adpter.out.web

import com.newy.algotrade.coroutine_based_application.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceHttpResponse

class ByBitLoadProductPriceHttpApi(private val client: HttpApiClient) : LoadProductPricePort {
    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        val (path, apiRateLimit) = ByBitHttpApiInfo.loadProductPrice()
        apiRateLimit.await()

        val response = client.get<ByBitProductPriceHttpResponse>(
            path = path,
            params = mapOf(
                "category" to param.category(),
                "symbol" to param.productCode,
                "interval" to param.intervalMinutes.toString(),
                "end" to param.endTime(),
                "limit" to param.limit.toString(),
            ),
            jsonExtraValues = ByBitProductPriceHttpResponse.jsonExtraValues(param.intervalMinutes),
        )

        return response.prices.sortedBy { it.time.begin }
    }
}
