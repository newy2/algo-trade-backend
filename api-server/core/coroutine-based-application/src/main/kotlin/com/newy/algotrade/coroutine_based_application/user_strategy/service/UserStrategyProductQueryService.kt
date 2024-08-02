package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyProductQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class UserStrategyProductQueryService(
    private val userStrategyProductPort: UserStrategyProductQueryPort
) : UserStrategyProductQuery {
    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> {
        return userStrategyProductPort.getAllUserStrategyKeys()
    }

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> {
        return userStrategyProductPort.getUserStrategyKeys(userStrategyId)
    }
}