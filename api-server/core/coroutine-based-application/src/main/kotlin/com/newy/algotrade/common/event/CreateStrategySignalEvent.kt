package com.newy.algotrade.common.event

import com.newy.algotrade.chart.domain.strategy.StrategySignal

data class CreateStrategySignalEvent(
    val userStrategyId: Long,
    val strategySignal: StrategySignal,
)