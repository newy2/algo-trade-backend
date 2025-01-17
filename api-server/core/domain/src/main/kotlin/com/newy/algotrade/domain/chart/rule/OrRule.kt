package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

class OrRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return rules.any { it.isSatisfied(index, history) }
    }
}