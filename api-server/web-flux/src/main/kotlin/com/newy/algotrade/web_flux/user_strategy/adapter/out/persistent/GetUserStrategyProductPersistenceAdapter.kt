package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyProductRepository
import kotlinx.coroutines.flow.toList

@PersistenceAdapter
class GetUserStrategyProductPersistenceAdapter(
    private val repository: UserStrategyProductRepository
) : UserStrategyProductQueryPort {
    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> =
        repository
            .findAllAsUserStrategyKey()
            .toList()

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> =
        repository
            .findByUserStrategyIdAsUserStrategyKey(userStrategyId)
            .toList()
}