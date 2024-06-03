package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface StrategySignalHistoryPort : GetStrategySignalHistoryPort, AddStrategySignalHistoryPort {
    fun remove(userStrategyId: String)
}

interface AddStrategySignalHistoryPort {
    fun add(userStrategyId: String, signal: StrategySignal)
}

interface GetStrategySignalHistoryPort {
    fun get(userStrategyId: String): StrategySignalHistory
}