package com.newy.algotrade.unit.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlePort
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals


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
        assertEquals(0, store.getCandles(productPriceKey).size)
    }

    @Test
    fun `candles 등록하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now)))

        store.getCandles(productPriceKey).let {
            assertEquals(1, it.size)
            assertEquals(productPriceWith(1000, now), it[0])
        }
    }

    @Test
    fun `candles 덮어쓰기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now.plusMinutes(0))))
        store.setCandles(productPriceKey, listOf(productPriceWith(2000, now.plusMinutes(1))))

        store.getCandles(productPriceKey).let {
            assertEquals(1, it.size)
            assertEquals(productPriceWith(2000, now.plusMinutes(1)), it[0])
        }
    }

    @Test
    fun `candles 등록 후 추가하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now.plusMinutes(0))))
        store.addCandles(productPriceKey, listOf(productPriceWith(2000, now.plusMinutes(1))))

        store.getCandles(productPriceKey).let {
            assertEquals(2, it.size)
            assertEquals(productPriceWith(1000, now), it[0])
            assertEquals(productPriceWith(2000, now.plusMinutes(1)), it[1])
        }
    }

    @Test
    fun `CandlePort#setCandles 을 호출하지 않으면, CandlePort#addCandles 의 호출은 무시된다`() = runBlocking {
        store.addCandles(productPriceKey, listOf(productPriceWith(1000, now)))

        assertEquals(0, store.getCandles(productPriceKey).size)
    }

    @Test
    fun `candles 삭제하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPriceWith(1000, now)))
        store.removeCandles(productPriceKey)

        assertEquals(0, store.getCandles(productPriceKey).size)
    }
}