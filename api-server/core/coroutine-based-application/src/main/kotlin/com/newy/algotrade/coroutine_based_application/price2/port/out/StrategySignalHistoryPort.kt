package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

interface StrategySignalHistoryPort : GetStrategySignalHistoryPort, AddStrategySignalHistoryPort {
    fun remove(userStrategyId: String)
}

interface AddStrategySignalHistoryPort {
    fun add(userStrategyId: String, signal: OrderSignal)
}

interface GetStrategySignalHistoryPort {
    fun get(userStrategyId: String): OrderSignalHistory
}