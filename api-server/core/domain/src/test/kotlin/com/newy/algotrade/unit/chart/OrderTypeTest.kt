package com.newy.algotrade.unit.chart

import com.newy.algotrade.domain.chart.OrderType
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