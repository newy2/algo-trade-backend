package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyProductQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.FindAllUserStrategyProductPort
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.FindUserStrategyProductPort
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class UserStrategyProductQueryService(
    private val findAllUserStrategyProductPort: FindAllUserStrategyProductPort,
    private val findUserStrategyProductPort: FindUserStrategyProductPort,
) : UserStrategyProductQuery {
    constructor(userStrategyProductPort: UserStrategyProductPort) : this(
        findAllUserStrategyProductPort = userStrategyProductPort,
        findUserStrategyProductPort = userStrategyProductPort,
    )

    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> {
        return findAllUserStrategyProductPort.findAllUserStrategyKeys()
    }

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> {
        return findUserStrategyProductPort.findUserStrategyKeys(userStrategyId)
    }
}