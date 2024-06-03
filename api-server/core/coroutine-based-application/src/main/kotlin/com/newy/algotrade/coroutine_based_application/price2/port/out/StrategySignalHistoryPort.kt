package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface StrategySignalHistoryPort : GetStrategySignalHistoryPort, AddStrategySignalHistoryPort {
    fun removeHistory(userStrategyId: String)
}

interface AddStrategySignalHistoryPort {
    fun addHistory(userStrategyId: String, signal: StrategySignal)
}

interface GetStrategySignalHistoryPort {
    fun getHistory(userStrategyId: String): StrategySignalHistory
}