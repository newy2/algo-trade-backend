package com.newy.algotrade.coroutine_based_application.product.adapter.out.web

import com.newy.algotrade.coroutine_based_application.auth.port.out.GetAccessTokenPort
import com.newy.algotrade.coroutine_based_application.common.consts.LsSecHttpApiInfo
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.post
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.LsSecProductPriceHttpResponse

class FetchLsSecProductPrice(
    private val client: HttpApiClient,
    private val accessTokenLoader: GetAccessTokenPort<PrivateApiInfo>,
    private val masterUserInfo: PrivateApiInfo,
) : ProductPriceQueryPort {
    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        val accessToken = accessTokenLoader.accessToken(masterUserInfo)

        val (path, apiRateLimit, trCode) = LsSecHttpApiInfo.loadProductPrice(param.isIntervalByDays())
        apiRateLimit.await()

        val response = client.post<LsSecProductPriceHttpResponse>(
            path = path,
            headers = mapOf(
                "Content-Type" to "application/json; charset=utf-8",
                "authorization" to "Bearer $accessToken",
                "tr_cd" to trCode,
                "tr_cont" to "N"
            ),
            body = mapOf(
                "${trCode}InBlock" to mapOf<String, Any>(
                    "shcode" to param.productCode,
                    "qrycnt" to param.limit,
                    "edate" to param.endTime(),
                    "comp_yn" to "N",
                ) + param.extraBody()
            ),
            jsonExtraValues = LsSecProductPriceHttpResponse.jsonExtraValues(
                trCode,
                param.intervalMinutes,
            ),
        )

        return response.prices
    }
}