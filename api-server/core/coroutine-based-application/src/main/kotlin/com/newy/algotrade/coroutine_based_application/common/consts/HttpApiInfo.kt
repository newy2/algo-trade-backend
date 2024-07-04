package com.newy.algotrade.coroutine_based_application.common.consts

import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiRateLimit
import com.newy.algotrade.domain.common.consts.LsSecTrCode

object ByBitHttpApiInfo {
    data class Result(val path: String, val apiRateLimit: HttpApiRateLimit)

    private val rateLimitMap = mapOf("/v5/market/kline" to HttpApiRateLimit(500))

    fun loadProductPrice(): Result {
        return "/v5/market/kline".let { path ->
            Result(path, rateLimitMap.getValue(path))
        }
    }
}

object LsSecHttpApiInfo {
    data class Result(val path: String, val apiRateLimit: HttpApiRateLimit, val trCode: String)

    private val rateLimitMap = mapOf("/stock/chart" to HttpApiRateLimit(1500))

    fun loadProductPrice(isIntervalByDays: Boolean): Result {
        return "/stock/chart".let { path ->
            Result(
                path,
                rateLimitMap.getValue(path),
                if (isIntervalByDays)
                    LsSecTrCode.GET_PRODUCT_PRICE_BY_DAY.code
                else
                    LsSecTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code
            )
        }
    }
}