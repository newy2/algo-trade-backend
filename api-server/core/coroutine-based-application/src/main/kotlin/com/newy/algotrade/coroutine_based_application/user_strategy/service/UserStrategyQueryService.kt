package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyQueryPort

open class UserStrategyQueryService(
    private val userStrategyPort: UserStrategyQueryPort
) : UserStrategyQuery {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        return userStrategyPort.getAllUserStrategies()
    }

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? {
        return userStrategyPort.getUserStrategy(userStrategyId)
    }
}