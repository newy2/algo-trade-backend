package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.Strategy
import helpers.BooleanRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.OffsetDateTime


private fun createOrderSignal(tradeType: OrderType) =
    OrderSignal(
        tradeType,
        Candle.TimeRange(Duration.ofMinutes(1), OffsetDateTime.now())
    )

class StrategyTest {
    private val index = 0
    private val entryType = OrderType.BUY

    private lateinit var emptyHistory: OrderSignalHistory
    private lateinit var enteredHistory: OrderSignalHistory
    private lateinit var exitedHistory: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = OrderSignalHistory()
        enteredHistory = OrderSignalHistory().also {
            it.add(createOrderSignal(entryType))
        }
        exitedHistory = OrderSignalHistory().also {
            it.add(createOrderSignal(entryType))
            it.add(createOrderSignal(entryType.completedType()))
        }
    }

    @Test
    fun `진입(enter) 신호가 발생하는 경우`() {
        val strategy = Strategy(
            entryOrderType = entryType,
            entryRule = BooleanRule(true),
            exitRule = BooleanRule(false),
        )

        assertEquals(OrderType.BUY, strategy.shouldOperate(index, emptyHistory))
        assertEquals(OrderType.NONE, strategy.shouldOperate(index, enteredHistory), "이미 진입했기 때문에 entry 신호를 무시한다")
        assertEquals(OrderType.BUY, strategy.shouldOperate(index, exitedHistory))
    }

    @Test
    fun `진출(exit) 신호가 발생하는 경우`() {
        val strategy = Strategy(
            entryOrderType = entryType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(true),
        )

        assertEquals(OrderType.NONE, strategy.shouldOperate(index, emptyHistory), "진출할 주문이 없으므로 exit 신호를 무시한다")
        assertEquals(OrderType.SELL, strategy.shouldOperate(index, enteredHistory))
        assertEquals(OrderType.NONE, strategy.shouldOperate(index, exitedHistory), "진출할 주문이 없으므로 exit 신호를 무시한다")
    }

    @Test
    fun `진입, 진출 신호가 발생하지 않는 경우`() {
        val strategy = Strategy(
            entryOrderType = entryType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(false),
        )

        assertEquals(OrderType.NONE, strategy.shouldOperate(index, emptyHistory))
        assertEquals(OrderType.NONE, strategy.shouldOperate(index, enteredHistory))
        assertEquals(OrderType.NONE, strategy.shouldOperate(index, exitedHistory))
    }

    @Test
    fun `진입, 진출 신호가 동시에 발생하는 경우`() {
        val strategy = Strategy(
            entryOrderType = entryType,
            entryRule = BooleanRule(true),
            exitRule = BooleanRule(true),
        )

        assertThrows<IllegalStateException>("enter, exit 신호가 동시에 발생하면 알고리즘 에러") {
            strategy.shouldOperate(index, emptyHistory)
        }
        assertThrows<IllegalStateException>("enter, exit 신호가 동시에 발생하면 알고리즘 에러") {
            strategy.shouldOperate(index, enteredHistory)
        }
        assertThrows<IllegalStateException>("enter, exit 신호가 동시에 발생하면 알고리즘 에러") {
            strategy.shouldOperate(index, exitedHistory)
        }
    }
}


class DifferentEntryOrderTypeTest {
    private val index = 0
    private val entryType = OrderType.SELL

    private lateinit var emptyHistory: OrderSignalHistory
    private lateinit var enteredHistory: OrderSignalHistory
    private lateinit var enteredHistoryWithDifferentOrderType: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = OrderSignalHistory()
        enteredHistory = OrderSignalHistory().also {
            it.add(createOrderSignal(entryType))
        }
        enteredHistoryWithDifferentOrderType = OrderSignalHistory().also {
            it.add(createOrderSignal(entryType.completedType()))
        }
    }


    @Test
    fun `entryType 이 다른 OrderHistory 를 사용하면 에러`() {
        val strategy = Strategy(
            entryOrderType = entryType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(false),
        )

        assertDoesNotThrow { strategy.shouldOperate(index, emptyHistory) }
        assertDoesNotThrow { strategy.shouldOperate(index, enteredHistory) }
        assertThrows<IllegalArgumentException>("entryType 이 다른 OrderHistory 를 사용하면 에러") {
            strategy.shouldOperate(index, enteredHistoryWithDifferentOrderType)
        }
    }
}

class ErrorTest {
    @Test
    fun `Strategy 생성자에 OrderType_NONE 을 전달할 수 없다`() {
        assertThrows<IllegalArgumentException> {
            Strategy(
                entryOrderType = OrderType.NONE,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
        assertDoesNotThrow {
            Strategy(
                entryOrderType = OrderType.BUY,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
        assertDoesNotThrow {
            Strategy(
                entryOrderType = OrderType.SELL,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
    }
}