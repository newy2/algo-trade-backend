package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.order.OrderType

@Deprecated("미사용 코드")
class StrategyRunner(
    private val candles: Candles,
    private val strategy: Strategy,
    private val history: StrategySignalHistory,
) {
    fun run(candleList: List<Candle>): StrategySignal {
        candles.upsert(candleList)

        return StrategySignal(
            strategy.shouldOperate(candles.lastIndex, history),
            candles.lastCandle.time,
            candles.lastCandle.price.close,
        ).also {
            if (it.orderType != OrderType.NONE) {
                history.add(it)
            }
        }
    }
}