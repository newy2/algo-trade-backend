package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.StrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

open class RunnableStrategyCommandService(
    private val candlesUseCase: SetCandlesUseCase,
    private val strategyUseCase: StrategyUseCase,
) : RunnableStrategyUseCase {
    override suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey) {
        candlesUseCase.setCandles(userStrategyKey.productPriceKey)
        strategyUseCase.setStrategy(userStrategyKey)
    }
}