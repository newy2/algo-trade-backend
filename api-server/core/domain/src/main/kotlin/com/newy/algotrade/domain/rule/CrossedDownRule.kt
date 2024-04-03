package com.newy.algotrade.domain.rule

import com.newy.algotrade.domain.chart.Indicator

class CrossedDownRule(lower: Indicator, upper: Indicator) : CrossedUpRule(upper, lower)