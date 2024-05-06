package com.newy.algotrade.unit.price.adapter.out.web.model.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceWebSocketResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ByBitProductPriceWebSocketResponseTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `가격 정보가 1개 있는 경우`() {
        val json = """
            {
                "topic": "kline.1.BTCUSDT",
                "data": [
                {
                    "start": 1672324800000,
                    "end": 1672325099999,
                    "interval": "1",
                    "open": "16649.5",
                    "close": "16677",
                    "high": "16677",
                    "low": "16608",
                    "volume": "2.081",
                    "turnover": "34666.4005",
                    "confirm": false,
                    "timestamp": 1672324988882
                }
                ],
                "ts": 1672324988882,
                "type": "snapshot"
            }
        """.trimIndent()

        val response = converter.toObject<ByBitProductPriceWebSocketResponse>(json)

        assertEquals(
            Candle.TimeFrame.M1(
                beginTime = 1672324800000,
                openPrice = "16649.5".toBigDecimal(),
                highPrice = "16677".toDouble().toBigDecimal(),
                lowPrice = "16608".toDouble().toBigDecimal(),
                closePrice = "16677".toDouble().toBigDecimal(),
                volume = "2.081".toBigDecimal(),
            ),
            response.price
        )
    }
}
