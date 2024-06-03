package com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

class InMemoryStrategySignalHistoryStore : StrategySignalHistoryPort {
    private val historyMap = mutableMapOf<String, StrategySignalHistory>()

    override fun get(userStrategyId: String): StrategySignalHistory {
        return historyMap[userStrategyId] ?: StrategySignalHistory()
    }

    override fun add(userStrategyId: String, signal: StrategySignal) {
        val history = historyMap[userStrategyId] ?: StrategySignalHistory().also { historyMap[userStrategyId] = it }
        history.add(signal)
    }

    override fun remove(userStrategyId: String) {
        historyMap.remove(userStrategyId)
    }
}