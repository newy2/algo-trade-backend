package com.newy.algotrade.domain.chart

import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

interface Rule {
    fun isSatisfied(index: Int, history: StrategySignalHistory? = null): Boolean
}