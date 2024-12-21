package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.auth.port.out.FindAccessTokenPort
import com.newy.algotrade.common.consts.LsSecHttpApiInfo
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam
import com.newy.algotrade.product_price.domain.jackson.LsSecProductPriceHttpResponse
import com.newy.algotrade.product_price.port.out.ProductPricePort

class FetchLsSecProductPrice(
    private val client: HttpApiClient,
    private val accessTokenLoader: FindAccessTokenPort<PrivateApiInfo>,
    private val masterUserInfo: PrivateApiInfo,
) : ProductPricePort {
    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        val accessToken = accessTokenLoader.findAccessToken(masterUserInfo)

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