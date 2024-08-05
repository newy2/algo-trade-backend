package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyPort
import com.newy.algotrade.domain.user_strategy.SetUserStrategy
import com.newy.algotrade.domain.user_strategy.SetUserStrategyKey
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.StrategyRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyR2dbcEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.UserStrategyRepository
import org.springframework.stereotype.Component

@Component
class UserStrategyPersistenceAdapter(
    private val strategyRepository: StrategyRepository,
    private val userStrategyRepository: UserStrategyRepository,
) : UserStrategyPort {
    override suspend fun setUserStrategy(setUserStrategy: SetUserStrategy): Long =
        userStrategyRepository.save(
            UserStrategyR2dbcEntity(
                domainEntity = setUserStrategy,
                strategyId = strategyRepository.findByClassName(setUserStrategy.setUserStrategyKey.strategyClassName)!!,
            )
        ).id

    override suspend fun hasUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean {
        return userStrategyRepository.existsByMarketAccountIdAndStrategyIdAndProductType(
            marketAccountId = setUserStrategyKey.marketServerAccountId,
            strategyId = strategyRepository.findByClassName(setUserStrategyKey.strategyClassName)!!,
            productType = setUserStrategyKey.productType.name,
        )
    }
}