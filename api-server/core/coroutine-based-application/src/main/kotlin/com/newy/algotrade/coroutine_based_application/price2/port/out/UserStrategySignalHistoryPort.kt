package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

interface UserStrategySignalHistoryPort : GetUserStrategySignalHistoryPort {
    fun add(userStrategyId: String, signal: OrderSignal)
    fun remove(userStrategyId: String)
}

interface GetUserStrategySignalHistoryPort {
    fun get(userStrategyId: String): OrderSignalHistory
}