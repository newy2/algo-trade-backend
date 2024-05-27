package com.newy.algotrade.unit.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlePort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals


private val now = OffsetDateTime.parse("2024-05-01T00:00Z")
fun productPrice(price: Int, beginTime: OffsetDateTime) =
    Candle.TimeFrame.M1(
        beginTime,
        price.toBigDecimal(),
        price.toBigDecimal(),
        price.toBigDecimal(),
        price.toBigDecimal(),
        0.toBigDecimal()
    )

private fun productPriceKey(productCode: String) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, Duration.ofMinutes(1))
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, Duration.ofMinutes(1))

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
        store.setCandles(productPriceKey, listOf(productPrice(1000, now)))

        store.getCandles(productPriceKey).let {
            assertEquals(1, it.size)
            assertEquals(productPrice(1000, now), it[0])
        }
    }

    @Test
    fun `candles 덮어쓰기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPrice(1000, now.plusMinutes(0))))
        store.setCandles(productPriceKey, listOf(productPrice(2000, now.plusMinutes(1))))

        store.getCandles(productPriceKey).let {
            assertEquals(1, it.size)
            assertEquals(productPrice(2000, now.plusMinutes(1)), it[0])
        }
    }

    @Test
    fun `candles 등록 후 추가하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPrice(1000, now.plusMinutes(0))))
        store.addCandles(productPriceKey, listOf(productPrice(2000, now.plusMinutes(1))))

        store.getCandles(productPriceKey).let {
            assertEquals(2, it.size)
            assertEquals(productPrice(1000, now), it[0])
            assertEquals(productPrice(2000, now.plusMinutes(1)), it[1])
        }
    }

    @Test
    fun `CandlePort#setCandles 을 호출하지 않으면, CandlePort#addCandles 의 호출은 무시된다`() = runBlocking {
        store.addCandles(productPriceKey, listOf(productPrice(1000, now)))

        assertEquals(0, store.getCandles(productPriceKey).size)
    }

    @Test
    fun `candles 삭제하기`() = runBlocking {
        store.setCandles(productPriceKey, listOf(productPrice(1000, now)))
        store.deleteCandles(productPriceKey)

        assertEquals(0, store.getCandles(productPriceKey).size)
    }
}