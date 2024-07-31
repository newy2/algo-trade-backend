package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.StrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

open class SetRunnableStrategyController(
    private val candlesUseCase: SetCandlesUseCase,
    private val strategyUseCase: StrategyUseCase,
) {
    suspend fun setUserStrategy(userStrategyKey: UserStrategyKey) {
        candlesUseCase.setCandles(userStrategyKey.productPriceKey)
        strategyUseCase.setStrategy(userStrategyKey)
    }
}