package com.newy.algotrade.integration.price.adapter.out.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.ByBitProductPriceHttpApi
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.GetProductPriceSelector
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceHttpResponse
import helpers.HttpApiClientByOkHttp
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.*
import kotlin.test.assertEquals

@DisplayName("상품 가격조회 API Response DTO")
class ByBitProductPriceResponseDtoTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())
    private val extraValues = ByBitProductPriceHttpResponse.jsonExtraValues(1)

    @Test
    fun `가격 정보가 없는 경우`() {
        val json = """
            {
                "retCode": 0,
                "retMsg": "OK",
                "result": {
                    "category": "spot",
                    "symbol": "BTCUSDT",
                    "list": []
                },
                "retExtInfo": {},
                "time": 1714575247899
            }
        """.trimIndent()

        val response = converter.toObject<ByBitProductPriceHttpResponse>(json, extraValues)

        assertEquals(listOf(), response.prices)
    }

    @Test
    fun `가격 정보가 1개 있는 경우`() {
        val json = """
            {
                "retCode": 0,
                "retMsg": "OK",
                "result": {
                    "category": "spot",
                    "symbol": "BTCUSDT",
                    "list": [
                        [
                            "1714575660000",
                            "58189.2",
                            "58495.08",
                            "58147.96",
                            "58266.31",
                            "311.07439",
                            "18120659.92086211"
                        ]
                    ]
                },
                "retExtInfo": {},
                "time": 1714575247899
            }
        """.trimIndent()

        val response = converter.toObject<ByBitProductPriceHttpResponse>(json, extraValues)

        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = Instant.ofEpochMilli("1714575660000".toLong()).atZone(ZoneOffset.UTC),
                    openPrice = "58189.2".toBigDecimal(),
                    highPrice = "58495.08".toBigDecimal(),
                    lowPrice = "58147.96".toBigDecimal(),
                    closePrice = "58266.31".toBigDecimal(),
                    volume = "311.07439".toBigDecimal()
                )
            ), response.prices
        )
    }
}

class ByBitProductPriceHttpApiTest {
    @Test
    fun `BTC 가격 조회 API`() = runBlocking {
        val client = GetProductPriceSelector(
            mapOf(
                "BY_BIT" to ByBitProductPriceHttpApi(
                    HttpApiClientByOkHttp(
                        OkHttpClient(),
                        TestEnv.ByBit.url,
                        JsonConverterByJackson(jacksonObjectMapper())
                    )
                )
            )
        )

        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = ZonedDateTime.parse("2024-05-01T00:00Z"),
                    openPrice = "59713.76".toBigDecimal(),
                    highPrice = "59771.9".toBigDecimal(),
                    lowPrice = "59539.96".toBigDecimal(),
                    closePrice = "59747.28".toBigDecimal(),
                    volume = "28.069189".toBigDecimal(),
                )
            ),
            client.productPrices(
                "BY_BIT",
                "spot",
                "BTCUSDT",
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-01T00:00Z"),
                1
            )
        )
    }
}