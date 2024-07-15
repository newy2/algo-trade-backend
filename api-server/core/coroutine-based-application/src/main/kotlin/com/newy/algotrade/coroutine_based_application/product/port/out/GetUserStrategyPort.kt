package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey

interface GetUserStrategyPort {
    suspend fun getAllUserStrategies(): List<UserStrategyKey>
}