package com.newy.algotrade.user_strategy.port.out

import com.newy.algotrade.user_strategy.domain.SetUserStrategy
import com.newy.algotrade.user_strategy.domain.SetUserStrategyKey

interface UserStrategyPort :
    SaveUserStrategyPort,
    ExistsUserStrategyPort

fun interface SaveUserStrategyPort {
    suspend fun saveUserStrategy(setUserStrategy: SetUserStrategy): Long
}

fun interface ExistsUserStrategyPort {
    suspend fun existsUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean
}