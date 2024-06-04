package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.order.OrderType

class StrategySignalHistory() {
    private val strategySignals = mutableListOf<StrategySignal>()

    fun add(strategySignal: StrategySignal): Boolean {
        if (strategySignals.isNotEmpty() && lastOrderType() == strategySignal.orderType) {
            return false
        }

        return strategySignals.add(strategySignal)
    }

    fun firstOrderType(): OrderType {
        if (strategySignals.isEmpty()) {
            return OrderType.NONE
        }

        return strategySignals.first().orderType
    }

    fun lastOrderType(): OrderType {
        if (strategySignals.isEmpty()) {
            return OrderType.NONE
        }

        return strategySignals.last().orderType
    }

    fun isEmpty(): Boolean {
        return firstOrderType() == OrderType.NONE
    }

    fun isOpened(): Boolean {
        val first = firstOrderType()
        val last = lastOrderType()

        return first != OrderType.NONE && first == last
    }

    fun lastStrategySignal(): StrategySignal {
        return strategySignals.last()
    }

    fun strategySignals(): List<StrategySignal> = strategySignals.toList()
}