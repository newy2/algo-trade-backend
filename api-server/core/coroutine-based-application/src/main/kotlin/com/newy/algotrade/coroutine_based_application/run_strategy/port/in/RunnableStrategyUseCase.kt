package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface RunnableStrategyUseCase :
    SetRunnableStrategyUseCase,
    RemoveRunnableStrategyUseCase

fun interface SetRunnableStrategyUseCase {
    suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey)
}

fun interface RemoveRunnableStrategyUseCase {
    suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey)
}