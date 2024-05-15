package com.newy.algotrade.domain.chart.order

class OrderSignalHistory() {
    private val orders = mutableListOf<OrderSignal>()

    fun add(orderSignal: OrderSignal): Boolean {
        if (orders.isNotEmpty() && lastOrderType() == orderSignal.type) {
            return false
        }

        return orders.add(orderSignal)
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