package com.newy.algotrade.domain.chart.order

import com.newy.algotrade.domain.chart.Candle

data class OrderSignal(val type: OrderType, val timeFrame: Candle.TimeRange)