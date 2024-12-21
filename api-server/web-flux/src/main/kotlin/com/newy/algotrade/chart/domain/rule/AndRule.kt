package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

class AndRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return rules.all { it.isSatisfied(index, history) }
    }
}