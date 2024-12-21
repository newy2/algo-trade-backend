package com.newy.algotrade.strategy.domain

import com.newy.algotrade.chart.domain.order.OrderType

data class Strategy(
    val id: Long,
    val className: String,
    val entryType: OrderType
)
