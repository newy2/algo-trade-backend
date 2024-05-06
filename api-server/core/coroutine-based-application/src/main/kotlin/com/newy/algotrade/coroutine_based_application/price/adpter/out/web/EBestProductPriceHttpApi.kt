package com.newy.algotrade.coroutine_based_application.price.adpter.out.web

import com.newy.algotrade.coroutine_based_application.auth.port.out.GetAccessTokenPort
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.post
import com.newy.algotrade.coroutine_based_application.price.port.out.GetProductPricePort
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.EBestTrCode
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.EBestProductPriceHttpResponse
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class EBestProductPriceHttpApi(
    client: HttpApiClient,
    private val accessTokenLoader: GetAccessTokenPort<PrivateApiInfo>,
    private val masterUserInfo: PrivateApiInfo,
) : GetProductPricePort(client) {
    override suspend fun productPrices(
        category: String,
        symbol: String,
        interval: Duration,
        endTime: OffsetDateTime,
        limit: Int,
    ): List<ProductPrice> {
        val (code, extraBody) = if (interval.toDays() >= 1) {
            Pair(EBestTrCode.GET_PRODUCT_PRICE_BY_DAY.code, mapOf("gubun" to "2"))
        } else {
            Pair(EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code, mapOf("ncnt" to interval.toMinutes()))
        }

        val accessToken = accessTokenLoader.accessToken(masterUserInfo)

        val response = client.post<EBestProductPriceHttpResponse>(
            path = "/stock/chart",
            headers = mapOf(
                "Content-Type" to "application/json; charset=utf-8",
                "authorization" to "Bearer $accessToken",
                "tr_cd" to code,
                "tr_cont" to "N"
            ),
            body = mapOf(
                "${code}InBlock" to mapOf<String, Any>(
                    "shcode" to symbol,
                    "qrycnt" to limit,
                    "edate" to endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    "comp_yn" to "N",
                ) + extraBody
            ),
            jsonExtraValues = EBestProductPriceHttpResponse.jsonExtraValues(
                code,
                interval.toMinutes(),
            ),
        )

        return response.prices
    }
}