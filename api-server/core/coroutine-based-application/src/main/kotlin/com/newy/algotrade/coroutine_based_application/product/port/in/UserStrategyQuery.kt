package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey

interface UserStrategyQuery : GetAllUserStrategyQuery, GetUserStrategyQuery

interface GetAllUserStrategyQuery {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}

interface GetUserStrategyQuery {
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}