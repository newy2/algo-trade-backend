package com.newy.algotrade.domain.chart.indicator

import java.math.BigDecimal

interface Indicator {
    // TODO 필요하다면 Indicator$get 리턴타입을 제너릭 적용할 것
    operator fun get(index: Int): BigDecimal
}