package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ConstDecimalIndicator
import com.newy.algotrade.chart.domain.indicator.Indicator
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

class UnderRule(private val first: Indicator, private val second: Indicator) : Rule {
    constructor(first: Indicator, second: Number) : this(first, ConstDecimalIndicator(second))

    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return first[index] < second[index]
    }
}
