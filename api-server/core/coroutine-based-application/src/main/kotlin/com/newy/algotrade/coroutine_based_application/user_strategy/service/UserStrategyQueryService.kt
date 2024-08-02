package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class UserStrategyQueryService(
    private val userStrategyPort: UserStrategyProductQueryPort
) : UserStrategyQuery {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        return userStrategyPort.getAllUserStrategies()
    }

    override suspend fun getUserStrategies(userStrategyId: Long): List<UserStrategyKey> {
        return userStrategyPort.getUserStrategies(userStrategyId)
    }
}