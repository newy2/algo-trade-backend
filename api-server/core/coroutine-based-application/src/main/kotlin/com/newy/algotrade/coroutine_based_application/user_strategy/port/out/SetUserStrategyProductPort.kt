package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface SetUserStrategyProductPort {
    suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean
}

interface UserStrategyProductQueryPort {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
    suspend fun getUserStrategies(userStrategyId: Long): List<UserStrategyKey>
}