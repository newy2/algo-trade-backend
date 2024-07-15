package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyPort
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyRepository
import org.springframework.stereotype.Component

@Component
class UserStrategyPersistenceAdapter(
    private val repository: UserStrategyRepository,
) : UserStrategyPort {
    override suspend fun setUserStrategy(
        marketServerAccountId: Long,
        strategyId: Long,
        productType: ProductType,
        productCategory: ProductCategory
    ): Long =
        repository.save(
            UserStrategyEntity(
                marketAccountId = marketServerAccountId,
                strategyId = strategyId,
                productType = productType.name,
                productCategory = productCategory.name,
            )
        ).id

    override suspend fun hasUserStrategy(
        marketServerAccountId: Long,
        strategyId: Long,
        productType: ProductType
    ): Boolean {
        return repository.existsByMarketAccountIdAndStrategyIdAndProductType(
            marketAccountId = marketServerAccountId,
            strategyId = strategyId,
            productType = productType.name,
        )
    }
}