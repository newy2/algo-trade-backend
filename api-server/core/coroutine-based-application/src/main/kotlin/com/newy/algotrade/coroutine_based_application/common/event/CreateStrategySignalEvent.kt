package com.newy.algotrade.coroutine_based_application.common.event

import com.newy.algotrade.domain.chart.strategy.StrategySignal

data class CreateStrategySignalEvent(
    val userStrategyId: Long,
    val strategySignal: StrategySignal,
)