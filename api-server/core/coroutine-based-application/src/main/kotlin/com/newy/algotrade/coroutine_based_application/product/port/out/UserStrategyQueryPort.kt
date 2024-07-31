package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

// TODO move to user_strategy package
interface UserStrategyQueryPort {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}