package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchProductPriceProxy
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.LsSecTrCode
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.LsSecProductPriceHttpResponse
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

@DisplayName("상품 가격조회 API Response DTO")
class LsSecProductPriceResponseDtoTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `분봉 차트 조회 Response`() {
        val json = """
            {
                "t8412OutBlock": {
                    "rec_count": 500
                },
                "t8412OutBlock1": [
                    {
                        "date": "20240429",
                        "time": "132300",
                        "open": 5030,
                        "high": 5080,
                        "low": 5020,
                        "close": 5040,
                        "jdiff_vol": 2933
                    }
                ]
            }
        """.trimIndent()

        val response = converter.toObject<LsSecProductPriceHttpResponse>(
            json,
            LsSecProductPriceHttpResponse.jsonExtraValues(
                code = LsSecTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code,
                interval = Duration.ofMinutes(1).toMinutes(),
            )
        )

        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = OffsetDateTime.parse("2024-04-29T13:22:00+09:00"),
                    openPrice = "5030.0".toBigDecimal(),
                    highPrice = "5080.0".toBigDecimal(),
                    lowPrice = "5020.0".toBigDecimal(),
                    closePrice = "5040.0".toBigDecimal(),
                    volume = "2933.0".toBigDecimal()
                )
            ), response.prices
        )
    }

    @Test
    fun `일봉 차트 조회 Response`() {
        val json = """
            {
                "t8410OutBlock": {
                    "e_time": "153000"
                },
                "t8410OutBlock1": [
                    {
                        "date": "20240429",
                        "open": 6480,
                        "high": 6550,
                        "low": 6390,
                        "close": 6540,
                        "jdiff_vol": 23239
                    }
                ]
            }
        """.trimIndent()


        val response = converter.toObject<LsSecProductPriceHttpResponse>(
            json,
            LsSecProductPriceHttpResponse.jsonExtraValues(
                code = LsSecTrCode.GET_PRODUCT_PRICE_BY_DAY.code,
                interval = Duration.ofDays(1).toMinutes(),
            )
        )

        assertEquals(
            listOf(
                Candle.TimeFrame.D1(
                    beginTime = OffsetDateTime.parse("2024-04-29T00:00+09:00"),
                    openPrice = "6480.0".toBigDecimal(),
                    highPrice = "6550.0".toBigDecimal(),
                    lowPrice = "6390.0".toBigDecimal(),
                    closePrice = "6540.0".toBigDecimal(),
                    volume = "23239.0".toBigDecimal()
                )
            ), response.prices
        )
    }
}

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
                GetProductPriceParam(
                    ProductPriceKey(
                        Market.LS_SEC,
                        ProductType.SPOT,
                        "078020",
                        Duration.ofMinutes(1),
                    ),
                    OffsetDateTime.parse("2024-05-03T00:00Z"),
                    1,
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
                GetProductPriceParam(
                    ProductPriceKey(
                        Market.LS_SEC,
                        ProductType.SPOT,
                        "078020",
                        Duration.ofDays(1),
                    ),
                    OffsetDateTime.parse("2024-05-03T00:00Z"),
                    1,
                )
            )
        )
    }
}