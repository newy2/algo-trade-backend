package com.newy.algotrade.domain.chart

import java.math.BigDecimal

interface Indicator {
    operator fun get(index: Int): BigDecimal
}