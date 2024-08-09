package com.newy.algotrade.domain.strategy

import com.newy.algotrade.domain.chart.order.OrderType

data class Strategy(
    val id: Long,
    val className: String,
    val entryType: OrderType
)
