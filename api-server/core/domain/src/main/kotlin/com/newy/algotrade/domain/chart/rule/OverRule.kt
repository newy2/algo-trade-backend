package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.ConstDecimalIndicator
import com.newy.algotrade.domain.chart.indicator.Indicator
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

class OverRule(private val first: Indicator, private val second: Indicator) : Rule {
    constructor(first: Indicator, second: Number) : this(first, ConstDecimalIndicator(second))

    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return first[index] > second[index]
    }

}
