package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.CreateStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.domain.chart.strategy.Strategy

class RegisterStrategyUseCase(
    private val candlePort: GetCandlePort,
    private val strategyPort: CreateStrategyPort,
) {
    fun register(key: UserStrategyKey) {
        val candles = candlePort.getCandles(key.productPriceKey)
        val strategy = Strategy.create(key.strategyId, candles)
        strategyPort.add(key, strategy)
    }
}

class UnRegisterStrategyUseCase(
    private val strategyPort: DeleteStrategyPort
) {
    fun unRegister(key: UserStrategyKey) {
        strategyPort.remove(key)
    }
}