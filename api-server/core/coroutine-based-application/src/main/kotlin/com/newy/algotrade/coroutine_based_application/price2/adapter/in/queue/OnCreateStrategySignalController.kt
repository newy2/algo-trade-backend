package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.queue

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddUserStrategySignalHistoryUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnCreateUserStrategySignalPort
import com.newy.algotrade.domain.chart.order.OrderSignal

class OnCreateStrategySignalController(
    private val userStrategySignalHistoryUseCase: AddUserStrategySignalHistoryUseCase
) : OnCreateUserStrategySignalPort {
    override fun onCreateSignal(userStrategyId: String, signal: OrderSignal) {
        userStrategySignalHistoryUseCase.addHistory(userStrategyId, signal)
    }
}