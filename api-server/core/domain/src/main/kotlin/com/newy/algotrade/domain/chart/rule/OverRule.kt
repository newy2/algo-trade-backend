package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Indicator
import com.newy.algotrade.domain.chart.Rule

class OverRule(private val first: Indicator, private val second: Indicator) : Rule {
    override fun isSatisfied(index: Int): Boolean {
        return first[index] > second[index]
    }

}
