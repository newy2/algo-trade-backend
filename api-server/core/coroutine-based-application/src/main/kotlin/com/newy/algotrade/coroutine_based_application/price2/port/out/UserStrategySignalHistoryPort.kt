package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

interface UserStrategySignalHistoryPort : GetUserStrategySignalHistoryPort, AddUserStrategySignalHistoryPort {
    fun remove(userStrategyId: String)
}

interface AddUserStrategySignalHistoryPort {
    fun add(userStrategyId: String, signal: OrderSignal)
}

interface GetUserStrategySignalHistoryPort {
    fun get(userStrategyId: String): OrderSignalHistory
}