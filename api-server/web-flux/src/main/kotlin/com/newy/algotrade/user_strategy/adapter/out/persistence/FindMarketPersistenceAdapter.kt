package com.newy.algotrade.user_strategy.adapter.out.persistence

import com.newy.algotrade.user_strategy.adapter.out.persistence.repository.MarketRepositoryForStrategy
import com.newy.algotrade.user_strategy.port.out.MarketPort
import org.springframework.stereotype.Component

@Component
class FindMarketPersistenceAdapter(
    private val marketRepository: MarketRepositoryForStrategy
) : MarketPort {
    override suspend fun findMarketIdsBy(marketAccountId: Long): List<Long> =
        marketRepository.findByMarketServerAccountId(marketAccountId).let {
            listOfNotNull(it?.parentMarketId, it?.id)
        }
}