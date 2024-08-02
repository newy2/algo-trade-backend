package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.UserStrategyQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.UserStrategyQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

// TODO user_strategy 로 이동
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