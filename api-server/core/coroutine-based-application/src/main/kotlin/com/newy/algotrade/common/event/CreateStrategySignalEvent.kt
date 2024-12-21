package com.newy.algotrade.common.event

import com.newy.algotrade.domain.chart.strategy.StrategySignal

data class CreateStrategySignalEvent(
    val userStrategyId: Long,
    val strategySignal: StrategySignal,
)