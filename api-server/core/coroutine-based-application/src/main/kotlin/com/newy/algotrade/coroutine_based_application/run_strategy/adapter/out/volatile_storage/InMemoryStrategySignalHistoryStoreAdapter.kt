package com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey

@ForTesting
open class InMemoryStrategySignalHistoryStoreAdapter : StrategySignalHistoryPort {
    private val historyMap = mutableMapOf<StrategySignalHistoryKey, StrategySignalHistory>()

    override suspend fun getHistory(key: StrategySignalHistoryKey): StrategySignalHistory {
        return historyMap[key] ?: StrategySignalHistory()
    }

    override suspend fun removeHistory(key: StrategySignalHistoryKey) {
        historyMap.remove(key)
    }

    override suspend fun addHistory(key: StrategySignalHistoryKey, signal: StrategySignal) {
        val history = historyMap[key] ?: StrategySignalHistory().also { historyMap[key] = it }
        history.add(signal)
    }
}