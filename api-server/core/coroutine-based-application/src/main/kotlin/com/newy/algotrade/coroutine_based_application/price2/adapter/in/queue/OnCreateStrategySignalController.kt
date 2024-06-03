package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.queue

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.AddStrategySignalHistoryUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnCreateStrategySignalPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal

class OnCreateStrategySignalController(
    private val strategySignalHistoryUseCase: AddStrategySignalHistoryUseCase
) : OnCreateStrategySignalPort {
    override fun onCreateSignal(userStrategyId: String, signal: StrategySignal) {
        strategySignalHistoryUseCase.addHistory(userStrategyId, signal)
    }
}