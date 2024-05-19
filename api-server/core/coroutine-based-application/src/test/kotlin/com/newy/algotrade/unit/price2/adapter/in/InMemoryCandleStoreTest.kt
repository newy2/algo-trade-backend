package com.newy.algotrade.unit.price2.adapter.`in`

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryCandlesStore
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlesPort
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


val now = OffsetDateTime.parse("2024-05-01T00:00Z")
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
    private lateinit var store: CandlesPort

    @BeforeEach
    fun setUp() {
        store = InMemoryCandlesStore()
    }

    @Test
    fun getCandles() = runBlocking {
        val candles = store.getCandles(key)

        assertEquals(0, candles.size)
    }

    @Test
    fun setCandles() = runBlocking {
        store.setCandles(key, listOf(productPrice(1000, now)))
        store.setCandles(key, listOf(productPrice(2000, now.plusMinutes(1))))

        val candles = store.getCandles(key)

        assertEquals(1, candles.size)
        assertEquals(productPrice(2000, now.plusMinutes(1)), candles[0])
    }

    @Test
    fun addCandles() = runBlocking {
        val store = InMemoryCandlesStore()
        val key = productPriceKey("BTCUSDT")

        store.addCandles(key, listOf(productPrice(1000, now)))
        store.addCandles(key, listOf(productPrice(2000, now.plusMinutes(1))))

        val candles = store.getCandles(key)

        assertEquals(2, candles.size)
        assertEquals(productPrice(1000, now), candles[0])
        assertEquals(productPrice(2000, now.plusMinutes(1)), candles[1])
    }
}