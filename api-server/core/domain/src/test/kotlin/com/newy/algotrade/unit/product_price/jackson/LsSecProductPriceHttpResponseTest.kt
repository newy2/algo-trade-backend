package com.newy.algotrade.unit.product_price.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.LsSecTrCode
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.product_price.jackson.LsSecProductPriceHttpResponse
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class LsSecProductPriceHttpResponseTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `분봉 차트 조회 Response`() {
        val rawResponse = """
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

        val response = LsSecProductPriceHttpResponse.jsonExtraValues(
            code = LsSecTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code,
            interval = Duration.ofMinutes(1).toMinutes(),
        ).let { jsonExtraValues ->
            converter.toObject<LsSecProductPriceHttpResponse>(rawResponse, jsonExtraValues)
        }

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
        val rawResponse = """
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


        val response = LsSecProductPriceHttpResponse.jsonExtraValues(
            code = LsSecTrCode.GET_PRODUCT_PRICE_BY_DAY.code,
            interval = Duration.ofDays(1).toMinutes(),
        ).let { jsonExtraValues ->
            converter.toObject<LsSecProductPriceHttpResponse>(rawResponse, jsonExtraValues)
        }

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
