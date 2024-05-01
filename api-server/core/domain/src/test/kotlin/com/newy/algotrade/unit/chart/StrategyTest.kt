package com.newy.algotrade.unit.chart

import com.newy.algotrade.domain.chart.Order
import com.newy.algotrade.domain.chart.OrderHistory
import com.newy.algotrade.domain.chart.OrderType
import com.newy.algotrade.domain.chart.Strategy
import helpers.BooleanRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


private fun createOrder(tradeType: OrderType) =
    Order(
        tradeType,
        price = 1000.toDouble().toBigDecimal(),
        quantity = 1.0,
    )

class StrategyTest {
    private val index = 0
    private val entryType = OrderType.BUY

    private lateinit var emptyHistory: OrderHistory
    private lateinit var enteredHistory: OrderHistory
    private lateinit var exitedHistory: OrderHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = OrderHistory()
        enteredHistory = OrderHistory().also {
            it.add(createOrder(entryType))
        }
        exitedHistory = OrderHistory().also {
            it.add(createOrder(entryType))
            it.add(createOrder(entryType.completedType()))
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

    private lateinit var emptyHistory: OrderHistory
    private lateinit var enteredHistory: OrderHistory
    private lateinit var enteredHistoryWithDifferentOrderType: OrderHistory

    @BeforeEach
    fun setUp() {
        emptyHistory = OrderHistory()
        enteredHistory = OrderHistory().also {
            it.add(createOrder(entryType))
        }
        enteredHistoryWithDifferentOrderType = OrderHistory().also {
            it.add(createOrder(entryType.completedType()))
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