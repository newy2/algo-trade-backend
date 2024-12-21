package com.newy.algotrade.chart.domain.strategy

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.order.OrderType
import java.math.BigDecimal

data class StrategySignal(
    val orderType: OrderType,
    val timeFrame: Candle.TimeRange,
    val price: BigDecimal,
)