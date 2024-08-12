package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.SetUserStrategy
import com.newy.algotrade.domain.user_strategy.SetUserStrategyKey

interface UserStrategyPort :
    SaveUserStrategyPort,
    ExistsUserStrategyPort

fun interface SaveUserStrategyPort {
    suspend fun saveUserStrategy(setUserStrategy: SetUserStrategy): Long
}

fun interface ExistsUserStrategyPort {
    suspend fun existsUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean
}