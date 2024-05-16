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

    fun isOpened(): Boolean {
        val first = firstOrderType()
        val last = lastOrderType()

        return first != OrderType.NONE && first == last
    }

    fun lastOrderSignal(): OrderSignal {
        return orders.last()
    }

    fun orders(): List<OrderSignal> = orders.toList()
}