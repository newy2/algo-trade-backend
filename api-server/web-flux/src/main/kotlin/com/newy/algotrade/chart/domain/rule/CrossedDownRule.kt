package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.indicator.ConstDecimalIndicator
import com.newy.algotrade.chart.domain.indicator.Indicator

class CrossedDownRule(lower: Indicator, upper: Indicator) : CrossedUpRule(upper, lower) {
    constructor(lower: Indicator, threshold: Number) : this(lower, ConstDecimalIndicator(threshold))
}