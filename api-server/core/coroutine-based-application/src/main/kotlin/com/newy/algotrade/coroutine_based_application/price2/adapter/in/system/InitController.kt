package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.user_strategy.GetAllUserStrategyQuery

class InitController(
    private val userStrategyQuery: GetAllUserStrategyQuery,
    private val candlesUseCase: SetCandlesUseCase,
    private val strategyUseCase: SetStrategyUseCase,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategies().forEach {
            candlesUseCase.setCandles(it.productPriceKey)
            strategyUseCase.setStrategy(it)
        }
    }
}