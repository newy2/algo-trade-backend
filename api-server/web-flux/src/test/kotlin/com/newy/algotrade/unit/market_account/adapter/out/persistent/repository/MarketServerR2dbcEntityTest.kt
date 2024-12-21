package com.newy.algotrade.unit.market_account.adapter.out.persistent.repository

import com.newy.algotrade.market_account.domain.MarketServer
import com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository.MarketServerR2dbcEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarketServerR2dbcEntityTest {
    @Test
    fun mapToDomainModel() {
        val persistentEntity = MarketServerR2dbcEntity(
            id = 1,
            marketId = 2
        )

        assertEquals(
            MarketServer(
                id = 1,
                marketId = 2
            ),
            persistentEntity.toDomainEntity()
        )
    }
}