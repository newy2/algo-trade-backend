package com.newy.algotrade.unit.product_price.adapter.out.volatile_storage

import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.volatile_storage.InMemoryCandlesStoreAdapter
import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlesPort
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


class InMemoryCandleStoreAdapterTest {
    private val productPriceKey = productPriceKey("BTCUSDT")
    private lateinit var store: CandlesPort

    @BeforeEach
    fun setUp() {
        store = InMemoryCandlesStoreAdapter()
    }

    @Test
    fun `등록하지 않은 candles 조회하기`() = runBlocking {
        assertCandleSize(expectedSize = 0)
    }

    @Test
    fun `candles 등록하기`() = runBlocking {
        store.saveWithReplaceCandles(productPriceKey, listOf(productPriceWith(price = 1000, beginTime = now)))

        assertCandleSize(expectedSize = 1)
        assertEquals(
            productPriceWith(price = 1000, beginTime = now),
            store.findCandles(productPriceKey)[0]
        )
    }

    @Test
    fun `candles 덮어쓰기`() = runBlocking {
        store.saveWithReplaceCandles(
            productPriceKey,
            listOf(productPriceWith(price = 1000, beginTime = now.plusMinutes(0)))
        )
        store.saveWithReplaceCandles(
            productPriceKey,
            listOf(productPriceWith(price = 2000, beginTime = now.plusMinutes(1)))
        )

        assertCandleSize(expectedSize = 1)
        assertEquals(
            productPriceWith(price = 2000, beginTime = now.plusMinutes(1)),
            store.findCandles(productPriceKey)[0]
        )
    }

    @Test
    fun `candles 등록 후 추가하기`() = runBlocking {
        store.saveWithReplaceCandles(
            productPriceKey,
            listOf(productPriceWith(price = 1000, beginTime = now.plusMinutes(0)))
        )
        store.saveWithAppendCandles(
            productPriceKey,
            listOf(productPriceWith(price = 2000, beginTime = now.plusMinutes(1)))
        )

        assertCandleSize(expectedSize = 2)
        store.findCandles(productPriceKey).let {
            assertEquals(productPriceWith(price = 1000, beginTime = now), it[0])
            assertEquals(productPriceWith(price = 2000, beginTime = now.plusMinutes(1)), it[1])
        }
    }

    @Test
    fun `CandlePort#setCandles 을 호출하지 않으면, CandlePort#addCandles 의 호출은 무시된다`() = runBlocking {
        store.saveWithAppendCandles(productPriceKey, listOf(productPriceWith(price = 1000, beginTime = now)))

        assertCandleSize(expectedSize = 0)
    }

    @Test
    fun `candles 삭제하기`() = runBlocking {
        store.saveWithReplaceCandles(productPriceKey, listOf(productPriceWith(price = 1000, beginTime = now)))
        store.deleteCandles(productPriceKey)

        assertCandleSize(expectedSize = 0)
    }

    private fun assertCandleSize(expectedSize: Int) {
        if (expectedSize == 0) {
            assertFalse(store.existsCandles(productPriceKey))
        } else {
            assertTrue(store.existsCandles(productPriceKey))
        }
        assertEquals(expectedSize, store.findCandles(productPriceKey).size)
    }
}