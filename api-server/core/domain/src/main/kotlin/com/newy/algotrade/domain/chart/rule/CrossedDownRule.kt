package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.indicator.ConstDecimalIndicator
import com.newy.algotrade.domain.chart.indicator.Indicator

class CrossedDownRule(lower: Indicator, upper: Indicator) : CrossedUpRule(upper, lower) {
    constructor(lower: Indicator, threshold: Number) : this(lower, ConstDecimalIndicator(threshold))
}