package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyProductPort :
    SaveAllUserStrategyProductPort,
    FindAllUserStrategyProductPort,
    FindUserStrategyProductPort

fun interface SaveAllUserStrategyProductPort {
    suspend fun saveAllUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean
}

fun interface FindAllUserStrategyProductPort {
    suspend fun findAllUserStrategyKeys(): List<UserStrategyKey>
}

fun interface FindUserStrategyProductPort {
    suspend fun findUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey>
}