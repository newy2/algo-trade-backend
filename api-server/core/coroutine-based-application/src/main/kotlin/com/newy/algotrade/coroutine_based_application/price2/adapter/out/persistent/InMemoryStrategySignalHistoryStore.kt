package com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class InMemoryStrategySignalHistoryStore : StrategySignalHistoryPort {
    private val historyMap = mutableMapOf<String, OrderSignalHistory>()

    override fun get(userStrategyId: String): OrderSignalHistory {
        return historyMap[userStrategyId] ?: OrderSignalHistory()
    }

    override fun add(userStrategyId: String, signal: OrderSignal) {
        val history = historyMap[userStrategyId] ?: OrderSignalHistory().also { historyMap[userStrategyId] = it }
        history.add(signal)
    }

    override fun remove(userStrategyId: String) {
        historyMap.remove(userStrategyId)
    }
}