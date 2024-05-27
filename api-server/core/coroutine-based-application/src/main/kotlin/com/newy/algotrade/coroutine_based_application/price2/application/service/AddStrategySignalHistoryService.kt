package com.newy.algotrade.coroutine_based_application.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddStrategySignalHistoryUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.AddStrategySignalHistoryPort
import com.newy.algotrade.domain.chart.order.OrderSignal

class AddStrategySignalHistoryService(
    private val strategySignalHistoryPort: AddStrategySignalHistoryPort
) : AddStrategySignalHistoryUseCase {
    override fun addHistory(userStrategyId: String, signal: OrderSignal) {
        strategySignalHistoryPort.add(userStrategyId, signal)
    }
}