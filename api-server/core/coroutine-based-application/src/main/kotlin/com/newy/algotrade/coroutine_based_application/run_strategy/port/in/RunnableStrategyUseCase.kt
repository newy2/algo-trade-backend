package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

interface RunnableStrategyUseCase {
    suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey)
}