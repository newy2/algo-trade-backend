package com.newy.algotrade.user_strategy.port.`in`

import com.newy.algotrade.user_strategy.domain.UserStrategyKey

interface UserStrategyProductQuery :
    GetAllUserStrategyProductQuery,
    GetUserStrategyProductQuery

fun interface GetAllUserStrategyProductQuery {
    suspend fun getAllUserStrategyKeys(): List<UserStrategyKey>
}

fun interface GetUserStrategyProductQuery {
    suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey>
}