package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey

interface UserStrategyQueryPort {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
    suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey?
}