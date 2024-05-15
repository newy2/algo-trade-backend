package com.newy.algotrade.domain.chart

data class OrderSignal(val type: OrderType, val timeFrame: Candle.TimeRange)