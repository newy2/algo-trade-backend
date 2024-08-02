package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyQueryPort {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}