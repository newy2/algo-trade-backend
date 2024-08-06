package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchProductPriceProxy
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.product.GetProductPriceHttpParam
import helpers.TestEnv
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class FetchLsSecProductPriceTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.LsSec.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val accessTokenLoader = LsSecAccessTokenHttpApi(client)
    private val api = FetchProductPriceProxy(
        mapOf(
            Market.LS_SEC to FetchLsSecProductPrice(
                client,
                accessTokenLoader,
                PrivateApiInfo(
                    key = TestEnv.LsSec.apiKey,
                    secret = TestEnv.LsSec.apiSecret,
                )
            )
        )
    )

    @Test
    fun `분봉 차트 조회 API`() = runBlocking {
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("2024-05-03T15:29+09:00"),
                    5020.0.toBigDecimal(),
                    5020.0.toBigDecimal(),
                    5020.0.toBigDecimal(),
                    5020.0.toBigDecimal(),
                    2650.0.toBigDecimal(),
                )
            ),
            api.getProductPrices(
                GetProductPriceHttpParam(
                    productPriceKey = productPriceKey(
                        productCode = "078020",
                        interval = Duration.ofMinutes(1)
                    ),
                    endTime = OffsetDateTime.parse("2024-05-03T00:00Z"),
                    limit = 1,
                )
            )
        )
    }

    @Test
    fun `일봉 차트 조회 API`() = runBlocking {
        assertEquals(
            listOf(
                Candle.TimeFrame.D1(
                    OffsetDateTime.parse("2024-05-03T00:00+09:00"),
                    5050.0.toBigDecimal(),
                    5070.0.toBigDecimal(),
                    4860.0.toBigDecimal(),
                    5020.0.toBigDecimal(),
                    104407.0.toBigDecimal(),
                )
            ),
            api.getProductPrices(
                GetProductPriceHttpParam(
                    productPriceKey = productPriceKey(
                        productCode = "078020",
                        interval = Duration.ofDays(1)
                    ),
                    endTime = OffsetDateTime.parse("2024-05-03T00:00Z"),
                    limit = 1,
                )
            )
        )
    }
}