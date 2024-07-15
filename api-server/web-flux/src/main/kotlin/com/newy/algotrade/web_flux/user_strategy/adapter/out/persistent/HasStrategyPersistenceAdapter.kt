package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.HasStrategyPort
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyRepository
import org.springframework.stereotype.Component

@Component
class HasStrategyPersistenceAdapter(
    private val repository: StrategyRepository,
) : HasStrategyPort {
    override suspend fun hasStrategyByClassName(strategyClassName: String): Boolean {
        return repository.existsByClassName(strategyClassName)
    }
}