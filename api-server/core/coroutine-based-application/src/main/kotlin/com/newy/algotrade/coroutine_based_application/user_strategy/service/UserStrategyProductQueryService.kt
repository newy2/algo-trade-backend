package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyProductQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.GetAllUserStrategyProductPort
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.GetUserStrategyProductPort
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class UserStrategyProductQueryService(
    private val getAllUserStrategyProductPort: GetAllUserStrategyProductPort,
    private val getUserStrategyProductPort: GetUserStrategyProductPort,
) : UserStrategyProductQuery {
    constructor(userStrategyProductPort: UserStrategyProductPort) : this(
        getAllUserStrategyProductPort = userStrategyProductPort,
        getUserStrategyProductPort = userStrategyProductPort,
    )

    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> {
        return getAllUserStrategyProductPort.getAllUserStrategyKeys()
    }

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> {
        return getUserStrategyProductPort.getUserStrategyKeys(userStrategyId)
    }
}