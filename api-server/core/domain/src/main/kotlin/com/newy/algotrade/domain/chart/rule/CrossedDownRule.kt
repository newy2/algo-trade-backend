package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.indicator.Indicator

class CrossedDownRule(lower: Indicator, upper: Indicator) : CrossedUpRule(upper, lower)