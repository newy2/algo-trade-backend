package com.newy.algotrade.domain.chart

import com.newy.algotrade.domain.chart.order.OrderSignalHistory

interface Rule {
    fun isSatisfied(index: Int, history: OrderSignalHistory? = null): Boolean
}