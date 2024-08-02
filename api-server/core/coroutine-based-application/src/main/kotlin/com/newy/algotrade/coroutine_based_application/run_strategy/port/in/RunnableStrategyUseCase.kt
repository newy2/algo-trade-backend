package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface RunnableStrategyUseCase {
    suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey)
    suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey)
}