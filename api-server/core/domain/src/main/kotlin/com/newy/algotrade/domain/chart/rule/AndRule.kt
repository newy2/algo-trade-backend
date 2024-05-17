package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class AndRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        return rules.all { it.isSatisfied(index, history) }
    }
}