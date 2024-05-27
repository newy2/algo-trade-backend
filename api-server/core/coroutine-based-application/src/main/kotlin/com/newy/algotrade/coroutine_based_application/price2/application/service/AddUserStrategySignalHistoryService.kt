package com.newy.algotrade.coroutine_based_application.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddUserStrategySignalHistoryUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.AddUserStrategySignalHistoryPort
import com.newy.algotrade.domain.chart.order.OrderSignal

class AddUserStrategySignalHistoryService(
    private val userStrategySignalHistoryPort: AddUserStrategySignalHistoryPort
) : AddUserStrategySignalHistoryUseCase {
    override fun addHistory(userStrategyId: String, signal: OrderSignal) {
        userStrategySignalHistoryPort.add(userStrategyId, signal)
    }
}