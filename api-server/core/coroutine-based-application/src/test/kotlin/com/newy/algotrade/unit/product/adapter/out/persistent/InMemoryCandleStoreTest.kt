package com.newy.algotrade.unit.product.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse


private val now = OffsetDateTime.parse("2024-05-01T00:00Z")
fun productPriceWith(price: Int, beginTime: OffsetDateTime) =
    productPrice(price, Duration.ofMinutes(1), beginTime)


class InMemoryCandleStoreTest {
    private val productPriceKey = productPriceKey("BTCUSDT")
    private lateinit var store: CandlePort

    @BeforeEach
    fun setUp() {
        store = InMemoryCandleStore()
    }

    @Test
    fun `등록하지 않은 candles 조회하기`() = runBlocking {
        assertCandleSize(expectedSize = 0)
    }

    @Test
    fun `candles 등록하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now)))

        assertCandleSize(expectedSize = 1)
        assertEquals(productPriceWith(1000, now), store.getCandles(productPriceKey)[0])
    }

    @Test
    fun `candles 덮어쓰기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now.plusMinutes(0))))
        store.setCandles(productPriceKey, listOf(productPriceWith(2000, now.plusMinutes(1))))

        assertCandleSize(expectedSize = 1)
        assertEquals(productPriceWith(2000, now.plusMinutes(1)), store.getCandles(productPriceKey)[0])
    }

    @Test
    fun `candles 등록 후 추가하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now.plusMinutes(0))))
        store.addCandles(productPriceKey, listOf(productPriceWith(2000, now.plusMinutes(1))))

        assertCandleSize(expectedSize = 2)
        store.getCandles(productPriceKey).let {
            assertEquals(productPriceWith(1000, now), it[0])
            assertEquals(productPriceWith(2000, now.plusMinutes(1)), it[1])
        }
    }

    @Test
    fun `CandlePort#setCandles 을 호출하지 않으면, CandlePort#addCandles 의 호출은 무시된다`() = runBlocking {
        store.addCandles(productPriceKey, listOf(productPriceWith(1000, now)))

        assertCandleSize(expectedSize = 0)
    }

    @Test
    fun `candles 삭제하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now)))
        store.removeCandles(productPriceKey)

        assertCandleSize(expectedSize = 0)
    }

    private fun assertCandleSize(expectedSize: Int) {
        if (expectedSize == 0) {
            assertFalse(store.hasCandles(productPriceKey))
        } else {
            assertTrue(store.hasCandles(productPriceKey))
        }
        assertEquals(expectedSize, store.getCandles(productPriceKey).size)
    }
}