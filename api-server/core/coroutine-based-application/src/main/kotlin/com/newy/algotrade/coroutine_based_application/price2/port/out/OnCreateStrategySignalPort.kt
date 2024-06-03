package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.chart.strategy.StrategySignal

interface OnCreateStrategySignalPort {
    fun onCreateSignal(userStrategyId: String, signal: StrategySignal)
}