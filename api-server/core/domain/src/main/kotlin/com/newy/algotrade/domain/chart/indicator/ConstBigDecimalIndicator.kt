package com.newy.algotrade.domain.chart.indicator

import java.math.BigDecimal

class ConstBigDecimalIndicator(private val constValue: BigDecimal) : Indicator {
    override fun get(index: Int) = constValue
}