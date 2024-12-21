package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

class OrRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return rules.any { it.isSatisfied(index, history) }
    }
}