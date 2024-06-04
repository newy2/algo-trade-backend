package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface StrategySignalHistoryPort : GetStrategySignalHistoryPort, AddStrategySignalHistoryPort {
    suspend fun removeHistory(userStrategyId: String)
}

interface AddStrategySignalHistoryPort {
    suspend fun addHistory(userStrategyId: String, signal: StrategySignal)
}

interface GetStrategySignalHistoryPort {
    suspend fun getHistory(userStrategyId: String): StrategySignalHistory
}