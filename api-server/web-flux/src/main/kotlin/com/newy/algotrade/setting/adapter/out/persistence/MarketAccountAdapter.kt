package com.newy.algotrade.setting.adapter.out.persistence

import com.newy.algotrade.setting.adapter.out.persistence.repository.MarketAccountR2dbcRepository
import com.newy.algotrade.setting.adapter.out.persistence.repository.MarketR2dbcRepository
import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.port.out.GetMarketAccountsOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter
import org.springframework.beans.factory.annotation.Autowired

@PersistenceAdapter("MarketAccountAdapterForSettingPackage")
class MarketAccountAdapter(
    @Autowired private val marketAccountR2dbcRepository: MarketAccountR2dbcRepository,
    @Autowired private val marketR2dbcRepository: MarketR2dbcRepository,
) : GetMarketAccountsOutPort {
    override suspend fun getMarketAccounts(userId: Long): List<MarketAccount> {
        val marketAccounts = marketAccountR2dbcRepository.findByUserIdAndUseYnOrderByIdAsc(userId)
        if (marketAccounts.isEmpty()) {
            return emptyList()
        }
        val markets = marketR2dbcRepository.findByIdIn(marketAccounts.map { it.marketId }.toSet())

        return marketAccounts.map {
            it.toDomainModel(markets)
        }
    }
}