package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.CreateStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.domain.chart.strategy.Strategy

class SetStrategyUseCase(
    private val candlePort: GetCandlePort,
    private val strategyPort: CreateStrategyPort,
) {
    fun setStrategy(key: UserStrategyKey) {
        val candles = candlePort.getCandles(key.productPriceKey)
        val strategy = Strategy.create(key.strategyId, candles)
        strategyPort.add(key, strategy)
    }
}
