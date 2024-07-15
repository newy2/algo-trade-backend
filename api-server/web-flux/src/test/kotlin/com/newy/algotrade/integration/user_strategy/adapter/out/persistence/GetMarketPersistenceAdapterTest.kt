package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.MarketAccountRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.GetMarketPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.MarketRepositoryForStrategy
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetMarketPersistenceAdapterTest(
    @Autowired private val marketAccountRepository: MarketAccountRepository,
    @Autowired private val marketRepository: MarketRepositoryForStrategy,
    @Autowired private val adapter: GetMarketPersistenceAdapter,
) : BaseDbTest() {
    @Test
    fun `등록한 marketAccountId 로 조회하기`() = runTransactional {
        val market = marketRepository.findByCode(Market.BY_BIT.name)!!
        val registeredMarketAccountId = marketAccountRepository.setMarketAccount(
            isProductionServer = false,
            code = Market.BY_BIT.name,
            appKey = "key",
            appSecret = "secret",
            displayName = "test",
        ).let {
            marketAccountRepository.getMarketAccountId(
                isProductionServer = false,
                code = Market.BY_BIT.name,
                appKey = "key",
                appSecret = "secret",
            )!!
        }
        val marketIds = adapter.getMarketIdsBy(registeredMarketAccountId)

        assertEquals(listOf(market.parentMarketId, market.id), marketIds)
    }

    @Test
    fun `등록하지 않은 marketAccountId 로 조회하기`() = runTransactional {
        val unRegisteredMarketAccountId = 100.toLong()
        val marketIds = adapter.getMarketIdsBy(unRegisteredMarketAccountId)

        assertEquals(emptyList<Long>(), marketIds)
    }
}