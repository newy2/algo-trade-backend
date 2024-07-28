package com.newy.algotrade.unit.market_account.adapter.out.persistent.repository

import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository.MarketAccountR2dbcEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarketAccountR2dbcEntityTest {
    @Test
    fun fromDomainModel() {
        val domainEntity = MarketAccount(
            userId = 1,
            marketServer = MarketServer(
                id = 2,
                marketId = 3
            ),
            displayName = "displayName",
            appKey = "appKey",
            appSecret = "appSecret",
        )

        assertEquals(
            MarketAccountR2dbcEntity(
                id = 0,
                userId = 1,
                marketServerId = 2,
                displayName = "displayName",
                appKey = "appKey",
                appSecret = "appSecret",
            ),
            MarketAccountR2dbcEntity(domainEntity)
        )
    }
}