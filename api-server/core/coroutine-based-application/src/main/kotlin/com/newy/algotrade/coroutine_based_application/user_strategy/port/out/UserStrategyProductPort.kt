package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyProductPort :
    SetUserStrategyProductPort,
    GetAllUserStrategyProductPort,
    GetUserStrategyProductPort

fun interface SetUserStrategyProductPort {
    suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean
}

fun interface GetAllUserStrategyProductPort {
    suspend fun getAllUserStrategyKeys(): List<UserStrategyKey>
}

fun interface GetUserStrategyProductPort {
    suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey>
}