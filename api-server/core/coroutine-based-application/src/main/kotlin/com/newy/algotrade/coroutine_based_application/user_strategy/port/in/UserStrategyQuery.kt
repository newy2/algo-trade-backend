package com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

interface UserStrategyQuery : GetAllUserStrategyQuery, GetUserStrategyQuery

interface GetAllUserStrategyQuery {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}

interface GetUserStrategyQuery {
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}