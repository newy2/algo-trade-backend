package com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand

interface SetUserStrategyUseCase {
    suspend fun setUserStrategy(strategy: SetUserStrategyCommand): Boolean
}