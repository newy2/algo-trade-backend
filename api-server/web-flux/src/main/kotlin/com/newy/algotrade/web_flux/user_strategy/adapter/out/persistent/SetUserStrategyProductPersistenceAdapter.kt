package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.SetUserStrategyProductPort
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyProductR2dbcEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyProductRepository
import kotlinx.coroutines.flow.collect
import org.springframework.stereotype.Component

@Component
class SetUserStrategyProductPersistenceAdapter(
    private val userStrategyProductRepository: UserStrategyProductRepository,
) : SetUserStrategyProductPort {
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
        userStrategyProductRepository.saveAll(products).collect()
        return true
    }
}