package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.CreateUserStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteUserStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.domain.chart.strategy.Strategy

class RegisterUserStrategyUseCase(
    private val candlePort: GetCandlePort,
    private val userStrategyPort: CreateUserStrategyPort,
) {
    fun register(key: UserStrategyKey) {
        val candles = candlePort.getCandles(key.productPriceKey)
        val strategy = Strategy.create(key.strategyId, candles)
        userStrategyPort.add(key, strategy)
    }
}

class UnRegisterUserStrategyUseCase(
    private val userStrategyPort: DeleteUserStrategyPort
) {
    fun unRegister(key: UserStrategyKey) {
        userStrategyPort.remove(key)
    }
}