package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

// TODO move to user_strategy package
interface UserStrategyQuery : GetAllUserStrategyQuery, GetUserStrategyQuery

interface GetAllUserStrategyQuery {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}

interface GetUserStrategyQuery {
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}