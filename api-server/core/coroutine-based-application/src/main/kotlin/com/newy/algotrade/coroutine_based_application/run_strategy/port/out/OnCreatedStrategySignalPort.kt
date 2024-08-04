package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal

fun interface OnCreatedStrategySignalPort {
    suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal)
}