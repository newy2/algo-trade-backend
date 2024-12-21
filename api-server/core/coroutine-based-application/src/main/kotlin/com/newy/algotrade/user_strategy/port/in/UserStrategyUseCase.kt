package com.newy.algotrade.user_strategy.port.`in`

import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand

interface UserStrategyUseCase :
    SetUserStrategyUseCase

fun interface SetUserStrategyUseCase {
    suspend fun setUserStrategy(strategy: SetUserStrategyCommand): Long
}