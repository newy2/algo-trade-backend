package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey

interface StrategySignalHistoryPort :
    GetStrategySignalHistoryPort,
    RemoveStrategySignalHistoryPort,
    AddStrategySignalHistoryPort

fun interface GetStrategySignalHistoryPort {
    suspend fun getHistory(key: StrategySignalHistoryKey): StrategySignalHistory
}

fun interface RemoveStrategySignalHistoryPort {
    suspend fun removeHistory(key: StrategySignalHistoryKey)
}

fun interface AddStrategySignalHistoryPort {
    suspend fun addHistory(key: StrategySignalHistoryKey, signal: StrategySignal)
}