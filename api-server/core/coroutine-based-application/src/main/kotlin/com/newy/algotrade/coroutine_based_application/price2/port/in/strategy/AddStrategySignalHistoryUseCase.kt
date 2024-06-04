package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.domain.chart.strategy.StrategySignal

interface AddStrategySignalHistoryUseCase {
    suspend fun addHistory(userStrategyId: String, signal: StrategySignal)
}