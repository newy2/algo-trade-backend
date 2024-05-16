package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule

class AndRule(
    vararg r: Rule,
    private val rules: Array<out Rule> = r,
) : Rule {

    override fun isSatisfied(index: Int): Boolean {
        return rules.all { it.isSatisfied(index) }
    }
}