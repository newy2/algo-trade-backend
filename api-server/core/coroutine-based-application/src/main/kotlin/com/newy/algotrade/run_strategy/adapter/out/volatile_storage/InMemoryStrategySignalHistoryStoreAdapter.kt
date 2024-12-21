package com.newy.algotrade.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey
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