package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.ConstBigDecimalIndicator
import com.newy.algotrade.domain.chart.indicator.Indicator
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class OverRule(private val first: Indicator, private val second: Indicator) : Rule {
    constructor(first: Indicator, second: Number) : this(first, ConstBigDecimalIndicator(second))
    
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        return first[index] > second[index]
    }

}
