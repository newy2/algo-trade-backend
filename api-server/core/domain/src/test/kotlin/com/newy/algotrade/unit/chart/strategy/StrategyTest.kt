package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import helpers.BooleanRule
import helpers.createOrderSignal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class StrategyTest {
    private val index = 0
    private val entryType = OrderType.BUY

    private lateinit var emptyHistory: StrategySignalHistory
    private lateinit var enteredHistory: StrategySignalHistory
    private lateinit var exitedHistory: StrategySignalHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = StrategySignalHistory()
        enteredHistory = StrategySignalHistory().also {
            it.add(createOrderSignal(entryType))
        }
        exitedHistory = StrategySignalHistory().also {
            it.add(createOrderSignal(entryType))
            it.add(createOrderSignal(entryType.completedType()))
        }
    }

    @Test
    fun `진입(enter) 신호가 발생하는 경우`() {
        val enterSignalStrategy = Strategy(
            entryType = entryType,
            entryRule = BooleanRule(true),
            exitRule = BooleanRule(false),
        )

        assertEquals(OrderType.BUY, enterSignalStrategy.shouldOperate(index, emptyHistory))
        assertEquals(
            OrderType.NONE,
            enterSignalStrategy.shouldOperate(index, enteredHistory),
            "이미 진입했기 때문에 entry 신호를 무시한다"
        )
        assertEquals(OrderType.BUY, enterSignalStrategy.shouldOperate(index, exitedHistory))
    }

    @Test
    fun `진출(exit) 신호가 발생하는 경우`() {
        val exitSignalStrategy = Strategy(
            entryType = entryType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(true),
        )

        assertEquals(
            OrderType.NONE,
            exitSignalStrategy.shouldOperate(index, emptyHistory),
            "진출할 주문이 없으므로 exit 신호를 무시한다"
        )
        assertEquals(OrderType.SELL, exitSignalStrategy.shouldOperate(index, enteredHistory))
        assertEquals(
            OrderType.NONE,
            exitSignalStrategy.shouldOperate(index, exitedHistory),
            "진출할 주문이 없으므로 exit 신호를 무시한다"
        )
    }

    @Test
    fun `진입, 진출 신호가 발생하지 않는 경우`() {
        val noneSignalStrategy = Strategy(
            entryType = entryType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(false),
        )

        assertEquals(OrderType.NONE, noneSignalStrategy.shouldOperate(index, emptyHistory))
        assertEquals(OrderType.NONE, noneSignalStrategy.shouldOperate(index, enteredHistory))
        assertEquals(OrderType.NONE, noneSignalStrategy.shouldOperate(index, exitedHistory))
    }
}


class DifferentEntryOrderTypeTest {
    private val index = 0
    private val shortPositionType = OrderType.SELL

    private lateinit var emptyHistory: StrategySignalHistory
    private lateinit var shortPositionHistory: StrategySignalHistory
    private lateinit var longPositionHistory: StrategySignalHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = StrategySignalHistory()
        shortPositionHistory = StrategySignalHistory().also {
            it.add(createOrderSignal(shortPositionType))
        }
        longPositionHistory = StrategySignalHistory().also {
            it.add(createOrderSignal(shortPositionType.completedType()))
        }
    }


    @Test
    fun `entryType 이 다른 OrderHistory 를 사용하면 에러`() {
        val strategy = Strategy(
            entryType = shortPositionType,
            entryRule = BooleanRule(false),
            exitRule = BooleanRule(false),
        )

        assertDoesNotThrow { strategy.shouldOperate(index, emptyHistory) }
        assertDoesNotThrow { strategy.shouldOperate(index, shortPositionHistory) }
        assertThrows<IllegalArgumentException>("entryType 이 다른 OrderHistory 를 사용하면 에러") {
            strategy.shouldOperate(index, longPositionHistory)
        }
    }
}

class ErrorTest {
    @Test
    fun `Strategy 생성자에 OrderType_NONE 을 전달할 수 없다`() {
        assertThrows<IllegalArgumentException> {
            Strategy(
                entryType = OrderType.NONE,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
        assertDoesNotThrow {
            Strategy(
                entryType = OrderType.BUY,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
        assertDoesNotThrow {
            Strategy(
                entryType = OrderType.SELL,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            )
        }
    }
}