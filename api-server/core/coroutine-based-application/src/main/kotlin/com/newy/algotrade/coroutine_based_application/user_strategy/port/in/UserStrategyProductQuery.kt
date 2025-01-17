package com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyProductQuery :
    GetAllUserStrategyProductQuery,
    GetUserStrategyProductQuery

fun interface GetAllUserStrategyProductQuery {
    suspend fun getAllUserStrategyKeys(): List<UserStrategyKey>
}

fun interface GetUserStrategyProductQuery {
    suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey>
}