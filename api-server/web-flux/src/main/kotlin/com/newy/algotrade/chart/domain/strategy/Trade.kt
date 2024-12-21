package com.newy.algotrade.chart.domain.strategy

import com.newy.algotrade.chart.domain.order.OrderType

class Trade(
    private val entrySignal: StrategySignal,
    private val exitSignal: StrategySignal
) {

    fun result(): TradeResult {
        val compare = exitSignal.price.compareTo(entrySignal.price).let {
            if (entrySignal.orderType == OrderType.BUY) it else it * -1
        }

        return when (compare) {
            1 -> TradeResult.WIN
            0 -> TradeResult.DRAW
            -1 -> TradeResult.LOSS
            else -> throw IllegalArgumentException("지원하지 않는 값입니다 (compare: $compare)")
        }
    }
}

enum class TradeResult {
    WIN,
    DRAW,
    LOSS;
}