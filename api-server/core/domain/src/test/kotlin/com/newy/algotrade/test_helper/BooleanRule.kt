package com.newy.algotrade.test_helper

import com.newy.algotrade.domain.chart.Rule

class BooleanRule(private val value: Boolean) : Rule {
    override fun isSatisfied(index: Int) = value
}