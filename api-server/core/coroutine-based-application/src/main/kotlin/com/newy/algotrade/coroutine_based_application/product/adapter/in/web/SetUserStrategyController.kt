package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.product.port.`in`.candle.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.model.UserStrategyKey

class SetUserStrategyController(
    private val candlesUseCase: SetCandlesUseCase,
    private val strategyUseCase: SetStrategyUseCase,
) {
    suspend fun setUserStrategy(userStrategyKey: UserStrategyKey) {
        candlesUseCase.setCandles(userStrategyKey.productPriceKey)
        strategyUseCase.setStrategy(userStrategyKey)
    }
}