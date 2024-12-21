package com.newy.algotrade.run_strategy.port.out

import com.newy.algotrade.chart.domain.strategy.StrategySignal

fun interface OnCreatedStrategySignalPort {
    suspend fun onCreatedSignal(userStrategyId: Long, signal: StrategySignal)
}