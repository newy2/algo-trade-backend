package com.newy.algotrade.coroutine_based_application.price2.application.service.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.AddStrategySignalHistoryUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.AddStrategySignalHistoryPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal

class AddStrategySignalHistoryService(
    private val strategySignalHistoryPort: AddStrategySignalHistoryPort
) : AddStrategySignalHistoryUseCase {
    override suspend fun addHistory(userStrategyId: String, signal: StrategySignal) {
        strategySignalHistoryPort.addHistory(userStrategyId, signal)
    }
}