package com.newy.algotrade.integration.price

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.price.ByBitProductPriceHttpApi
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import helpers.HttpApiClientByOkHttp
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class ByBitProductPriceHttpApiTest {
    @Test
    fun `BTC 가격 조회 API`() = runBlocking {
        val client = ByBitProductPriceHttpApi(
            HttpApiClientByOkHttp(
                OkHttpClient(),
                TestEnv.ByBit.url,
                JsonConverterByJackson(jacksonObjectMapper())
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
                "spot",
                "BTCUSDT",
                Duration.ofMinutes(1),
                ZonedDateTime.parse("2024-05-01T00:00Z"),
                1
            )
        )
    }
}