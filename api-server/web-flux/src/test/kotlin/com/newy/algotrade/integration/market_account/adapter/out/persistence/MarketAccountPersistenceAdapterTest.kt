package com.newy.algotrade.integration.market_account.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.MarketAccountPersistenceAdapter
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.MarketAccountRepository
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MarketAccountPersistenceAdapterTest(
    @Autowired private val repository: MarketAccountRepository,
) : BaseDbTest() {
    private val adapter = MarketAccountPersistenceAdapter(repository)

    @Test
    fun `사용자 계정 등록하기`() = runTransactional {
        val account = SetMarketAccountCommand(
            market = Market.BY_BIT,
            isProduction = false,
            displayName = "name",
            appKey = "key",
            appSecret = "secret"
        )

        assertFalse(adapter.hasMarketAccount(account))
        adapter.setMarketAccount(account)
        assertTrue(adapter.hasMarketAccount(account))
    }
}