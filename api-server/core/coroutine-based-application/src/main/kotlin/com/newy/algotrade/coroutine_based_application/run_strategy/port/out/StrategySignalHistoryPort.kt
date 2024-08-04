package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface StrategySignalHistoryPort :
    GetStrategySignalHistoryPort,
    RemoveStrategySignalHistoryPort,
    AddStrategySignalHistoryPort

fun interface GetStrategySignalHistoryPort {
    suspend fun getHistory(userStrategyId: String): StrategySignalHistory
}

fun interface RemoveStrategySignalHistoryPort {
    suspend fun removeHistory(userStrategyId: String)
}

fun interface AddStrategySignalHistoryPort {
    suspend fun addHistory(userStrategyId: String, signal: StrategySignal)
}