package com.newy.algotrade.chart.domain

import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

interface Rule {
    fun isSatisfied(index: Int, history: StrategySignalHistory? = null): Boolean
}