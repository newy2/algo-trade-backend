package com.newy.algotrade.coroutine_based_application.common.consts

import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiRateLimit
import com.newy.algotrade.domain.common.consts.EBestTrCode

object ByBitHttpApiInfo {
    data class Result(val path: String, val apiRateLimit: HttpApiRateLimit)

    private val infos: Map<String, Result> = mapOf(
        "/v5/market/kline" to Result("/v5/market/kline", HttpApiRateLimit(500)),
    )

    fun loadProductPrice(): Result {
        return infos.getValue("/v5/market/kline")
    }
}

object EBestHttpApiInfo {
    data class Result(val path: String, val apiRateLimit: HttpApiRateLimit, val trCode: String)

    private val infos = mapOf(
        EBestTrCode.GET_PRODUCT_PRICE_BY_DAY to Result(
            "/stock/chart",
            HttpApiRateLimit(1500),
            EBestTrCode.GET_PRODUCT_PRICE_BY_DAY.code
        ),
        EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE to Result(
            "/stock/chart",
            HttpApiRateLimit(1500),
            EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code
        )
    )

    fun loadProductPrice(isIntervalByDays: Boolean): Result {
        val trCode =
            if (isIntervalByDays) EBestTrCode.GET_PRODUCT_PRICE_BY_DAY else EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE

        return infos.getValue(trCode)
    }
}