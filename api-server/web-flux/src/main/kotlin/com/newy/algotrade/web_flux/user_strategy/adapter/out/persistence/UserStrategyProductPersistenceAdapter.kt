package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductPort
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyProductR2dbcEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class UserStrategyProductPersistenceAdapter(
    private val repository: UserStrategyProductRepository,
) : UserStrategyProductPort {
    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean {
        if (productIds.isEmpty()) {
            return false
        }

        val products = productIds.mapIndexed { index, productId ->
            UserStrategyProductR2dbcEntity(
                userStrategyId = userStrategyId,
                productId = productId,
                sort = index + 1
            )
        }
        repository.saveAll(products).collect()
        return true
    }

    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> =
        repository
            .findAllAsUserStrategyKey()
            .toList()

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> =
        repository
            .findByUserStrategyIdAsUserStrategyKey(userStrategyId)
            .toList()
}