package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.order.OrderSignal

interface OnCreateUserStrategySignalPort {
    fun onCreateSignal(userStrategyId: String, orderSignal: OrderSignal)
}