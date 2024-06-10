package com.newy.algotrade.coroutine_based_application.product.port.`in`.user_strategy

import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.model.UserStrategyKey

interface GetAllUserStrategyQuery {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}
