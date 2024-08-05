package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.GetMarketPort
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.MarketRepositoryForStrategy
import org.springframework.stereotype.Component

@Component
class GetMarketPersistenceAdapter(
    private val marketRepository: MarketRepositoryForStrategy
) : GetMarketPort {
    override suspend fun getMarketIdsBy(marketAccountId: Long): List<Long> =
        marketRepository.findByMarketServerAccountId(marketAccountId).let {
            listOfNotNull(it?.parentMarketId, it?.id)
        }
}