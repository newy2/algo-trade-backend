package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import helpers.createOrderSignal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class StrategySignalHistoryTest {
    private lateinit var history: StrategySignalHistory

    @BeforeEach
    fun setUp() {
        history = StrategySignalHistory()
    }

    @Test
    fun `빈 StrategyHistory 인 경우`() {
        assertTrue(history.isEmpty())
        assertFalse(history.isOpened())
        assertEquals(OrderType.NONE, history.firstOrderType())
        assertEquals(OrderType.NONE, history.lastOrderType())
    }

    @Test
    fun `Buy OrderType 을 먼저 추가하면 long 포지션 히스토리가 된다`() {
        val isAdded = createOrderSignal(OrderType.BUY).let { entrySignal ->
            history.add(entrySignal)
        }

        assertTrue(isAdded)
        assertFalse(history.isEmpty())
        assertTrue(history.isOpened())
        assertEquals(OrderType.BUY, history.firstOrderType())
        assertEquals(OrderType.BUY, history.lastOrderType())
    }

    @Test
    fun `Sell OrderType 을 먼저 추가하면 short 포지션 히스토리가 된다`() {
        val isAdded = createOrderSignal(OrderType.SELL).let { entrySignal ->
            history.add(entrySignal)
        }

        assertTrue(isAdded)
        assertFalse(history.isEmpty())
        assertTrue(history.isOpened())
        assertEquals(OrderType.SELL, history.firstOrderType())
        assertEquals(OrderType.SELL, history.lastOrderType())
    }

    @Test
    fun `같은 OrderType 는 이어서 추가할 수 없다`() {
        val entrySignal = createOrderSignal(OrderType.BUY)

        assertTrue(history.add(entrySignal), "첫 번째 add")
        assertFalse(history.add(entrySignal), "두 번째 add")
    }

    @Test
    fun `다른 OrderType 는 이어서 추가할 수 있다`() {
        val entrySignal = createOrderSignal(OrderType.BUY)
        val exitSignal = createOrderSignal(OrderType.SELL)

        assertTrue(history.add(entrySignal))
        assertTrue(history.add(exitSignal))
        assertEquals(OrderType.BUY, history.firstOrderType())
        assertEquals(OrderType.SELL, history.lastOrderType())
    }
}