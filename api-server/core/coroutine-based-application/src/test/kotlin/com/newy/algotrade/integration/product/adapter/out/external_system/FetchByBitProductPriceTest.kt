package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.product_price.adapter.out.external_system.FetchByBitProductPrice
import com.newy.algotrade.product_price.adapter.out.external_system.FetchProductPriceProxyAdapter
import helpers.TestEnv
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class FetchByBitProductPriceTest {
    private val client = FetchProductPriceProxyAdapter(
        mapOf(
            Market.BY_BIT to FetchByBitProductPrice(
                DefaultHttpApiClient(
                    OkHttpClient(),
                    TestEnv.ByBit.url,
                    JsonConverterByJackson(jacksonObjectMapper())
                )
            )
        )
    )

    @Test
    fun `현물 BTC 가격 조회 API`() = runBlocking {
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    openPrice = "59713.76".toBigDecimal(),
                    highPrice = "59771.9".toBigDecimal(),
                    lowPrice = "59539.96".toBigDecimal(),
                    closePrice = "59747.28".toBigDecimal(),
                    volume = "28.069189".toBigDecimal(),
                )
            ),
            client.fetchProductPrices(
                GetProductPriceHttpParam(
                    productPriceKey = productPriceKey(
                        productCode = "BTCUSDT",
                        interval = Duration.ofMinutes(1),
                    ),
                    endTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    limit = 1,
                )
            )
        )
    }

    @Test
    fun `1일봉 - 현물 BTC 가격 조회 API`() = runBlocking {
        assertEquals(
            listOf(
                Candle.TimeFrame.D1(
                    beginTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    openPrice = "59713.76".toBigDecimal(),
                    highPrice = "75100.0".toBigDecimal(),
                    lowPrice = "52241.02".toBigDecimal(),
                    closePrice = "58034.47".toBigDecimal(),
                    volume = "291706.443849".toBigDecimal(),
                )
            ),
            client.fetchProductPrices(
                GetProductPriceHttpParam(
                    productPriceKey = productPriceKey(
                        productCode = "BTCUSDT",
                        interval = Duration.ofDays(1),
                    ),
                    endTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    limit = 1,
                )
            )
        )
    }

    @Test
    fun `무기한 선물 BTC 가격 조회 API`() = runBlocking {
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    openPrice = "60686.4".toBigDecimal(),
                    highPrice = "60758.9".toBigDecimal(),
                    lowPrice = "60673.4".toBigDecimal(),
                    closePrice = "60737.2".toBigDecimal(),
                    volume = "0.632".toBigDecimal(),
                )
            ),
            client.fetchProductPrices(
                GetProductPriceHttpParam(
                    productPriceKey = productPriceKey(
                        productCode = "BTCUSDT",
                        productType = ProductType.PERPETUAL_FUTURE,
                        interval = Duration.ofMinutes(1),
                    ),
                    endTime = OffsetDateTime.parse("2024-05-01T00:00Z"),
                    limit = 1,
                )
            )
        )
    }
}