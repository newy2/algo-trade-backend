package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal

interface OnCreatedStrategySignalPort {
    suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal)
}