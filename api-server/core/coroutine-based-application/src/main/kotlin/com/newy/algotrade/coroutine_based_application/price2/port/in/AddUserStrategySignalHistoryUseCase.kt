package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.domain.chart.order.OrderSignal

interface AddUserStrategySignalHistoryUseCase {
    fun addHistory(userStrategyId: String, signal: OrderSignal)
}