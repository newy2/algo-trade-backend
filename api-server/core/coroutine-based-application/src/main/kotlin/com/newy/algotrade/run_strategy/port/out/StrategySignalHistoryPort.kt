package com.newy.algotrade.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey

interface StrategySignalHistoryPort :
    FindStrategySignalHistoryPort,
    DeleteStrategySignalHistoryPort,
    SaveStrategySignalHistoryPort

interface FindStrategySignalHistoryPort {
    suspend fun findHistory(key: StrategySignalHistoryKey, maxSize: Int = 10): StrategySignalHistory
}

fun interface DeleteStrategySignalHistoryPort {
    suspend fun deleteHistory(key: StrategySignalHistoryKey)
}

fun interface SaveStrategySignalHistoryPort {
    suspend fun saveHistory(key: StrategySignalHistoryKey, signal: StrategySignal)
}