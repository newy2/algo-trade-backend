package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage.InMemoryCandleStoreAdapter
import com.newy.algotrade.coroutine_based_application.product.service.GetCandlesQueryService
import helpers.productPrice
import helpers.productPriceKey
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class GetCandlesQueryServiceTest {
    @Test
    fun `getCandles 메소드 테스트`() {
        val beginTime = OffsetDateTime.parse("2024-05-01T00:00Z")
        val candlesPort = InMemoryCandleStoreAdapter().also {
            it.setCandles(
                productPriceKey(productCode = "BTCUSDT"),
                listOf(productPrice(amount = 1000, interval = Duration.ofMinutes(1), beginTime))
            )
            it.setCandles(
                productPriceKey(productCode = "005930"),
                listOf(
                    productPrice(amount = 2000, interval = Duration.ofMinutes(1), beginTime),
                    productPrice(amount = 3000, interval = Duration.ofMinutes(1), beginTime.plusMinutes(1)),
                )
            )
        }

        val service = GetCandlesQueryService(candlesPort)

        service.getCandles(productPriceKey("BTCUSDT")).let {
            assertEquals(1, it.size)
            assertEquals(productPrice(amount = 1000, interval = Duration.ofMinutes(1), beginTime), it[0])
        }
        service.getCandles(productPriceKey("005930")).let {
            assertEquals(2, it.size)
            assertEquals(productPrice(amount = 2000, interval = Duration.ofMinutes(1), beginTime), it[0])
            assertEquals(productPrice(amount = 3000, interval = Duration.ofMinutes(1), beginTime.plusMinutes(1)), it[1])
        }
    }
}