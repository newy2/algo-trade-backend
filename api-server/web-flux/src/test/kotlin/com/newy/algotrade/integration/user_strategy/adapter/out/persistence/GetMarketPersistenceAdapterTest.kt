package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.user_strategy.adapter.out.persistence.FindMarketPersistenceAdapter
import com.newy.algotrade.user_strategy.adapter.out.persistence.repository.MarketRepositoryForStrategy
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

class GetMarketPersistenceAdapterTest(
    @Autowired private val marketRepository: MarketRepositoryForStrategy,
    @Autowired private val adapter: FindMarketPersistenceAdapter,
) : BaseGetMarketPersistenceAdapterTest() {
    @Test
    fun `등록한 marketAccountId 로 조회하기`() = runTransactional {
        val market = marketRepository.findByCode(Market.BY_BIT.name)!!
        val registeredMarketAccountId = saveMarketAccount(Market.BY_BIT).id

        val marketIds = adapter.findMarketIdsBy(registeredMarketAccountId)

        assertEquals(listOf(market.parentMarketId, market.id), marketIds)
    }

    @Test
    fun `등록하지 않은 marketAccountId 로 조회하기`() = runTransactional {
        val unRegisteredMarketAccountId = 100.toLong()
        val marketIds = adapter.findMarketIdsBy(unRegisteredMarketAccountId)

        assertEquals(emptyList<Long>(), marketIds)
    }
}

open class BaseGetMarketPersistenceAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var marketAccountUseCase: SetMarketAccountUseCase

    protected suspend fun saveMarketAccount(market: Market) =
        marketAccountUseCase.setMarketAccount(
            SetMarketAccountCommand(
                userId = getAdminUserId(),
                market = market,
                isProduction = false,
                displayName = "displayName",
                appKey = "appKey",
                appSecret = "appSecret",
            )
        )

    private suspend fun getAdminUserId(): Long {
        // TODO UserRepository 구현 시, 리팩토링 하기
        val adminUser = databaseClient
            .sql(
                """
                SELECT id
                FROM   users
                WHERE  email = 'admin'
            """.trimIndent()
            )
            .fetch()
            .awaitSingle()

        return adminUser["id"] as Long
    }
}