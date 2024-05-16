package com.newy.algotrade.unit.chart.order

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertFalse

private fun createOrderSignal(tradeType: OrderType) =
    OrderSignal(
        tradeType,
        Candle.TimeRange(Duration.ofMinutes(1), OffsetDateTime.now()),
        1000.0.toBigDecimal()
    )

class OrderSignalHistoryTest {
    private lateinit var history: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        history = OrderSignalHistory()
    }

    @Test
    fun `빈 OrderHistory 인 경우`() {
        assertFalse(history.isOpened())
        assertEquals(OrderType.NONE, history.firstOrderType())
        assertEquals(OrderType.NONE, history.lastOrderType())
    }

    @Test
    fun `먼저 Buy Order 를 추가한 경우`() {
        val isAdded = history.add(createOrderSignal(OrderType.BUY))

        assertTrue(isAdded)
        assertTrue(history.isOpened())
        assertEquals(OrderType.BUY, history.firstOrderType())
        assertEquals(OrderType.BUY, history.lastOrderType())
    }

    @Test
    fun `먼저 Sell Order 를 추가한 경우`() {
        val isAdded = history.add(createOrderSignal(OrderType.SELL))

        assertTrue(isAdded)
        assertTrue(history.isOpened())
        assertEquals(OrderType.SELL, history.firstOrderType())
        assertEquals(OrderType.SELL, history.lastOrderType())
    }

    @Test
    fun `같은 OrderType 는 이어서 추가할 수 없다`() {
        val firstAdded = history.add(createOrderSignal(OrderType.BUY))
        val secondAdded = history.add(createOrderSignal(OrderType.BUY))

        assertTrue(firstAdded)
        Assertions.assertFalse(secondAdded)
    }

    @Test
    fun `다른 OrderType 는 이어서 추가할 수 있다`() {
        val firstAdded = history.add(createOrderSignal(OrderType.BUY))
        val secondAdded = history.add(createOrderSignal(OrderType.SELL))

        assertTrue(firstAdded)
        assertTrue(secondAdded)
        assertEquals(OrderType.BUY, history.firstOrderType())
        assertEquals(OrderType.SELL, history.lastOrderType())
    }
}