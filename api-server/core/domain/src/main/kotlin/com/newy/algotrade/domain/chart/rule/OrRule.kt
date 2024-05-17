package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class OrRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        return rules.any { it.isSatisfied(index, history) }
    }
}