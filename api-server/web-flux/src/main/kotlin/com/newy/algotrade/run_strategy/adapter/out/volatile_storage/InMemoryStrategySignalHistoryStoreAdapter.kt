package com.newy.algotrade.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import com.newy.algotrade.common.annotation.ForTesting
import com.newy.algotrade.run_strategy.domain.StrategySignalHistoryKey
import com.newy.algotrade.run_strategy.port.out.StrategySignalHistoryPort

@ForTesting
open class InMemoryStrategySignalHistoryStoreAdapter : StrategySignalHistoryPort {
    private val historyMap = mutableMapOf<StrategySignalHistoryKey, StrategySignalHistory>()

    override suspend fun findHistory(key: StrategySignalHistoryKey, maxSize: Int): StrategySignalHistory {
        return historyMap[key] ?: StrategySignalHistory()
    }

    override suspend fun deleteHistory(key: StrategySignalHistoryKey) {
        historyMap.remove(key)
    }

    override suspend fun saveHistory(key: StrategySignalHistoryKey, signal: StrategySignal) {
        val history = historyMap[key] ?: StrategySignalHistory().also { historyMap[key] = it }
        history.add(signal)
    }
}