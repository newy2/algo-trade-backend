package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface RunnableStrategyUseCase :
    SetRunnableStrategyUseCase,
    RemoveRunnableStrategy

fun interface SetRunnableStrategyUseCase {
    suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey)
}

fun interface RemoveRunnableStrategy {
    suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey)
}