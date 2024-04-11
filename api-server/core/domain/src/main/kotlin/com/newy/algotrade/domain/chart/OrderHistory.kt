package com.newy.algotrade.domain.chart

class OrderHistory() {
    private val orders = mutableListOf<Order>()

    fun add(order: Order): Boolean {
        if (orders.isNotEmpty() && lastOrderType() == order.type) {
            return false
        }

        return orders.add(order)
    }

    fun firstOrderType(): OrderType {
        if (orders.isEmpty()) {
            return OrderType.NONE
        }

        return orders.first().type
    }

    fun lastOrderType(): OrderType {
        if (orders.isEmpty()) {
            return OrderType.NONE
        }

        return orders.last().type
    }
}