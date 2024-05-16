package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.Indicator

class UnderRule(private val first: Indicator, private val second: Indicator) : Rule {
    override fun isSatisfied(index: Int): Boolean {
        return first[index] < second[index]
    }

}
