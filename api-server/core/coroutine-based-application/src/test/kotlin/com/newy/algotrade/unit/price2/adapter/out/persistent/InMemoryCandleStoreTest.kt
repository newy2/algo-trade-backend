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

fun productPriceKey(productCode: String) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, Duration.ofMinutes(1))
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, Duration.ofMinutes(1))

class InMemoryCandleStoreTest {
    private val key = productPriceKey("BTCUSDT")
    private lateinit var store: CandlePort

    @BeforeEach
    fun setUp() {
        store = InMemoryCandleStore()
    }

    @Test
    fun `등록하지 않은 candles 조회하기`() = runBlocking {
        assertEquals(0, store.getCandles(key).size)
    }

    @Test
    fun `candles 덮어쓰기`() = runBlocking {
        store.setCandles(key, listOf(productPrice(1000, now)))
        store.setCandles(key, listOf(productPrice(2000, now.plusMinutes(1))))

        store.getCandles(key).let {
            assertEquals(1, it.size)
            assertEquals(productPrice(2000, now.plusMinutes(1)), it[0])
        }
    }

    @Test
    fun `candles 추가하기`() = runBlocking {
        store.addCandles(key, listOf(productPrice(1000, now)))
        store.addCandles(key, listOf(productPrice(2000, now.plusMinutes(1))))

        store.getCandles(key).let {
            assertEquals(2, it.size)
            assertEquals(productPrice(1000, now), it[0])
            assertEquals(productPrice(2000, now.plusMinutes(1)), it[1])
        }
    }

    @Test
    fun `candles 삭제하기`() = runBlocking {
        store.addCandles(key, listOf(productPrice(1000, now)))
        store.deleteCandles(key)

        assertEquals(0, store.getCandles(key).size)
    }
}