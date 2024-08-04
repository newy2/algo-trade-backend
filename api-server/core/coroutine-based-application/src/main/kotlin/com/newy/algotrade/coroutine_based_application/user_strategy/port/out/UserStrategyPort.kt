package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.SetUserStrategy
import com.newy.algotrade.domain.user_strategy.SetUserStrategyKey

interface UserStrategyPort :
    SetUserStrategyPort,
    HasUserStrategyPort

fun interface SetUserStrategyPort {
    suspend fun setUserStrategy(setUserStrategy: SetUserStrategy): Long
}

fun interface HasUserStrategyPort {
    suspend fun hasUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean
}