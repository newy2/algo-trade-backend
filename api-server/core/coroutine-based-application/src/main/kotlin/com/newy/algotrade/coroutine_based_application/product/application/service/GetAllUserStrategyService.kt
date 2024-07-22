package com.newy.algotrade.coroutine_based_application.product.application.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.GetUserStrategyPort

open class GetAllUserStrategyService(
    private val userStrategyPort: GetUserStrategyPort
) : GetAllUserStrategyQuery {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        return userStrategyPort.getAllUserStrategies()
    }
}