package com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`

import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface UserStrategyQuery : GetAllUserStrategyQuery, GetUserStrategyQuery

interface GetAllUserStrategyQuery {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}

interface GetUserStrategyQuery {
    suspend fun getUserStrategies(userStrategyId: Long): List<UserStrategyKey>
}