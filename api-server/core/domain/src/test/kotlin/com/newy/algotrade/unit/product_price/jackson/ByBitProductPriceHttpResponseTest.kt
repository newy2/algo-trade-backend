package com.newy.algotrade.unit.product_price.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import com.newy.algotrade.common.domain.mapper.toObject
import com.newy.algotrade.product_price.domain.jackson.ByBitProductPriceHttpResponse
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class ByBitProductPriceHttpResponseTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())
    private val extraValues = ByBitProductPriceHttpResponse.jsonExtraValues(interval = 1)

    @Test
    fun `가격 정보가 없는 경우`() {
        val rawResponse = """
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

        val response = converter.toObject<ByBitProductPriceHttpResponse>(rawResponse, extraValues)

        assertEquals(emptyList(), response.prices)
    }

    @Test
    fun `가격 정보가 1개 있는 경우`() {
        val rawResponse = """
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

        val response = converter.toObject<ByBitProductPriceHttpResponse>(rawResponse, extraValues)

        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = Instant.ofEpochMilli("1714575660000".toLong()).atOffset(ZoneOffset.UTC),
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