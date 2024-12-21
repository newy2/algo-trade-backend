package com.newy.algotrade.user_strategy.service

import com.newy.algotrade.user_strategy.domain.UserStrategyKey
import com.newy.algotrade.user_strategy.port.`in`.UserStrategyProductQuery
import com.newy.algotrade.user_strategy.port.out.FindAllUserStrategyProductPort
import com.newy.algotrade.user_strategy.port.out.FindUserStrategyProductPort
import com.newy.algotrade.user_strategy.port.out.UserStrategyProductPort

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