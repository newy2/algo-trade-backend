package com.newy.algotrade.unit.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey
import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategySignalHistoryStoreAdapter
import com.newy.algotrade.run_strategy.port.out.StrategySignalHistoryPort
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class InMemoryStrategySignalHistoryStoreAdapterTest {
    private val key = StrategySignalHistoryKey(
        userStrategyId = 1,
        productPriceKey = productPriceKey("BTCUSDT")
    )
    private val signal = StrategySignal(
        OrderType.BUY,
        Candle.TimeRange(
            Duration.ofMinutes(1),
            OffsetDateTime.parse("2024-05-09T00:00+09:00")
        ),
        1000.toBigDecimal()
    )

    private lateinit var store: StrategySignalHistoryPort

    @BeforeEach
    fun setUp() = runBlocking {
        store = InMemoryStrategySignalHistoryStoreAdapter().also {
            it.saveHistory(key, signal)
        }
    }

    @Test
    fun `등록한 히스토리 가져오기`() = runTest {
        store.findHistory(key).strategySignals().let {
            assertEquals(1, it.size)
            assertEquals(signal, it.first())
        }
    }

    @Test
    fun `등록한 히스토리 삭제하기`() = runTest {
        store.deleteHistory(key)

        assertTrue(store.findHistory(key).isEmpty())
    }

    @Test
    fun `등록하지 않은 히스토리 가져오기`() = runTest {
        val unRegisteredKey = key.copy(userStrategyId = 2)

        assertTrue(store.findHistory(unRegisteredKey).isEmpty())
    }
}