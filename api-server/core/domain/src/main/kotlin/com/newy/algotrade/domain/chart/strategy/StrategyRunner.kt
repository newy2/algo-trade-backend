package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType

class StrategyRunner(
    private val candles: Candles,
    private val strategy: Strategy,
    private val history: OrderSignalHistory,
) {
    fun run(candleList: List<Candle>): OrderSignal {
        candles.upsert(candleList)

        return OrderSignal(
            strategy.shouldOperate(candles.lastIndex, history),
            candles.lastCandle.time,
            candles.lastCandle.price.close,
        ).also {
            if (it.type != OrderType.NONE) {
                history.add(it)
            }
        }
    }
}