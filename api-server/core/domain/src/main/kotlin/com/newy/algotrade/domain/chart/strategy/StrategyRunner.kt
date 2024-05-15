package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class StrategyRunner(
    private val candles: Candles,
    private val strategy: Strategy,
    private val orderHistory: OrderSignalHistory,
) {
    fun run(candleList: List<Candle>): OrderSignal {
        candles.upsert(candleList)

        return OrderSignal(
            strategy.shouldOperate(candles.lastIndex, orderHistory),
            candles.lastCandle.time
        )
    }
}