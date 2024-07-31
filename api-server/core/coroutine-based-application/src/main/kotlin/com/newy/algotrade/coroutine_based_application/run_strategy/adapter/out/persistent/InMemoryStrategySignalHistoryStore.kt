package com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

open class InMemoryStrategySignalHistoryStore : StrategySignalHistoryPort {
    private val historyMap = mutableMapOf<String, StrategySignalHistory>()

    override suspend fun getHistory(userStrategyId: String): StrategySignalHistory {
        return historyMap[userStrategyId] ?: StrategySignalHistory()
    }

    override suspend fun addHistory(userStrategyId: String, signal: StrategySignal) {
        val history = historyMap[userStrategyId] ?: StrategySignalHistory().also { historyMap[userStrategyId] = it }
        history.add(signal)
    }

    override suspend fun removeHistory(userStrategyId: String) {
        historyMap.remove(userStrategyId)
    }
}