package com.newy.algotrade.domain.rule

import com.newy.algotrade.domain.chart.Indicator
import com.newy.algotrade.domain.chart.Rule

class UnderRule(private val first: Indicator, private val second: Indicator) : Rule {
    override fun isSatisfied(index: Int): Boolean {
        return first[index] < second[index]
    }

}
