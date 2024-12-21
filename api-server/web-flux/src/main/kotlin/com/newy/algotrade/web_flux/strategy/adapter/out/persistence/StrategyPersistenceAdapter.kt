package com.newy.algotrade.web_flux.strategy.adapter.out.persistence

import com.newy.algotrade.strategy.domain.Strategy
import com.newy.algotrade.strategy.port.out.StrategyPort
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.strategy.adapter.out.persistence.repository.StrategyRepository
import kotlinx.coroutines.flow.toList

@PersistenceAdapter
class StrategyPersistenceAdapter(
    private val repository: StrategyRepository
) : StrategyPort {
    override suspend fun findAllStrategies(): List<Strategy> =
        repository.findAll().toList().map { it.toDomainEntity() }

    override suspend fun findStrategy(className: String): Strategy? =
        repository.findByClassName(className)?.toDomainEntity()

    override suspend fun existsStrategy(className: String): Boolean =
        repository.existsByClassName(className)
}