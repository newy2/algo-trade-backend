package com.newy.algotrade.unit.market_account.adapter.out.persistent.repository

import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository.MarketAccountR2dbcEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarketAccountR2dbcEntityTest {
    private val persistenceEntity = MarketAccountR2dbcEntity(
        id = 0,
        userId = 1,
        marketServerId = 2,
        displayName = "displayName",
        appKey = "appKey",
        appSecret = "appSecret",
    )

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

        assertEquals(persistenceEntity, MarketAccountR2dbcEntity(domainEntity))
    }

    @Test
    fun toDomainModel() {
        val marketServer = MarketServer(
            id = 2,
            marketId = 3
        )

        assertEquals(
            MarketAccount(
                userId = 1,
                marketServer = marketServer,
                displayName = "displayName",
                appKey = "appKey",
                appSecret = "appSecret",
            ),
            persistenceEntity.toDomainEntity(marketServer)
        )
    }
}