package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface StrategySignalHistoryPort : StrategySignalHistoryQueryPort, StrategySignalHistoryCommandPort

interface StrategySignalHistoryCommandPort {
    suspend fun removeHistory(userStrategyId: String)
    suspend fun addHistory(userStrategyId: String, signal: StrategySignal)
}

interface StrategySignalHistoryQueryPort {
    suspend fun getHistory(userStrategyId: String): StrategySignalHistory
}