package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import java.math.BigDecimal

data class StrategySignal(
    val orderType: OrderType,
    val timeFrame: Candle.TimeRange,
    val price: BigDecimal,
)