package com.newy.algotrade.chart.domain.indicator

import java.math.BigDecimal

class ConstDecimalIndicator(
    private val constValue: BigDecimal
) : Indicator {
    constructor(constValue: Number) : this(constValue.toDouble().toBigDecimal())

    override fun get(index: Int) = constValue
}