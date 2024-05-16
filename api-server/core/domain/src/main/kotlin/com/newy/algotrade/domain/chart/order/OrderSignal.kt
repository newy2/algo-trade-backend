package com.newy.algotrade.domain.chart.order

import com.newy.algotrade.domain.chart.Candle
import java.math.BigDecimal

data class OrderSignal(
    val type: OrderType,
    val timeFrame: Candle.TimeRange,
    val orderPrice: BigDecimal,
)