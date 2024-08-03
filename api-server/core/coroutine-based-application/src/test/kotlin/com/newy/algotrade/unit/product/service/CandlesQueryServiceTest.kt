package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage.InMemoryCandleStoreAdapter
import com.newy.algotrade.coroutine_based_application.product.service.CandlesQueryService
import helpers.productPrice
import helpers.productPriceKey
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class CandlesQueryServiceTest {
    @Test
    fun `getCandles 메소드 테스트`() {
        val beginTime = OffsetDateTime.parse("2024-05-01T00:00Z")
        val candlesPort = InMemoryCandleStoreAdapter().also {
            it.setCandles(
                productPriceKey("BTCUSDT"),
                listOf(productPrice(1000, Duration.ofMinutes(1), beginTime))
            )
            it.setCandles(
                productPriceKey("005930"),
                listOf(
                    productPrice(2000, Duration.ofMinutes(1), beginTime),
                    productPrice(3000, Duration.ofMinutes(1), beginTime.plusMinutes(1)),
                )
            )
        }

        val service = CandlesQueryService(candlesPort)

        service.getCandles(productPriceKey("BTCUSDT")).let {
            assertEquals(1, it.size)
            assertEquals(productPrice(1000, Duration.ofMinutes(1), beginTime), it[0])
        }
        service.getCandles(productPriceKey("005930")).let {
            assertEquals(2, it.size)
            assertEquals(productPrice(2000, Duration.ofMinutes(1), beginTime), it[0])
            assertEquals(productPrice(3000, Duration.ofMinutes(1), beginTime.plusMinutes(1)), it[1])
        }
    }
}