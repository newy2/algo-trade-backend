package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.order.OrderType

class StrategySignalHistory() {
    private val orders = mutableListOf<StrategySignal>()

    fun add(orderSignal: StrategySignal): Boolean {
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

    fun isEmpty(): Boolean {
        return firstOrderType() == OrderType.NONE
    }

    fun isOpened(): Boolean {
        val first = firstOrderType()
        val last = lastOrderType()

        return first != OrderType.NONE && first == last
    }

    fun lastOrderSignal(): StrategySignal {
        return orders.last()
    }

    fun orders(): List<StrategySignal> = orders.toList()
}