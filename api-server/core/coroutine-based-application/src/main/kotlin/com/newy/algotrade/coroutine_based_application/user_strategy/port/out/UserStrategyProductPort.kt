package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyProductPort : UserStrategyProductCommandPort, UserStrategyProductQueryPort

interface UserStrategyProductCommandPort {
    suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean
}

interface UserStrategyProductQueryPort {
    suspend fun getAllUserStrategyKeys(): List<UserStrategyKey>
    suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey>
}