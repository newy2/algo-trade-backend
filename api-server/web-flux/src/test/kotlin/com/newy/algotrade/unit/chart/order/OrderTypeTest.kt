package com.newy.algotrade.unit.chart.order

import com.newy.algotrade.chart.domain.order.OrderType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrderTypeTest {
    @Test
    fun completedType() {
        assertEquals(OrderType.SELL, OrderType.BUY.completedType())
        assertEquals(OrderType.BUY, OrderType.SELL.completedType())
        assertEquals(OrderType.NONE, OrderType.NONE.completedType())
    }
}