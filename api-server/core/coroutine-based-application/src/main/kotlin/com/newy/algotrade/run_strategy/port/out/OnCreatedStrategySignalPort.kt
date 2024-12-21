package com.newy.algotrade.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal

fun interface OnCreatedStrategySignalPort {
    suspend fun onCreatedSignal(userStrategyId: Long, signal: StrategySignal)
}