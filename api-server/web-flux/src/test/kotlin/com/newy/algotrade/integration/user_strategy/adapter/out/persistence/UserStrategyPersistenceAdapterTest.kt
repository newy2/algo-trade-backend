package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.UserStrategyPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyRepository
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

class UserStrategyPersistenceAdapterTest(
    @Autowired private val userStrategyRepository: UserStrategyRepository,
    @Autowired private val adapter: UserStrategyPersistenceAdapter,
) : BaseUserStrategyPersistenceAdapterTest() {
    @Test
    fun `userStrategy 등록하기`() = runTransactional {
        val (marketAccountId, strategyClassName, strategyId) = setInitData()

        val userStrategyId = adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
            timeFrame = Candle.TimeFrame.M1,
        )

        assertEquals(
            UserStrategyEntity(
                id = userStrategyId,
                marketAccountId = marketAccountId,
                strategyId = strategyId,
                productType = ProductType.SPOT.name,
                productCategory = ProductCategory.USER_PICK.name,
                timeFrame = Candle.TimeFrame.M1.name,
            ),
            userStrategyRepository.findById(userStrategyId)!!
        )
    }

    @Test
    fun `등록한 userStrategy 확인하기`() = runTransactional {
        val (marketAccountId, strategyClassName) = setInitData()

        val beforeSaved = adapter.hasUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
        )

        adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
            timeFrame = Candle.TimeFrame.M1,
        )

        val afterSaved = adapter.hasUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
        )

        assertFalse(beforeSaved)
        assertTrue(afterSaved)
    }

    @Test
    fun `중복된 userStrategy 를 등록하는 경우`() = runTransactional {
        // TODO Remove this? ("등록한 userStrategy 확인하기" 테스트 때문에 지워도 될듯)
        val (marketAccountId, strategyClassName) = setInitData()

        adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
            timeFrame = Candle.TimeFrame.M1,
        )

        assertThrows<DataIntegrityViolationException> {
            val sameMarketAccountId = marketAccountId
            val sameStrategyClassName = strategyClassName
            val sameProductType = ProductType.SPOT

            adapter.setUserStrategy(
                marketServerAccountId = sameMarketAccountId,
                strategyClassName = sameStrategyClassName,
                productType = sameProductType,
                productCategory = ProductCategory.TOP_TRADING_VALUE,
                timeFrame = Candle.TimeFrame.M1,
            )
        }
    }
}

open class BaseUserStrategyPersistenceAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var strategyRepository: StrategyRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var marketAccountUseCase: SetMarketAccountUseCase

    protected suspend fun setInitData(): Triple<Long, String, Long> {
        val marketAccountId = marketAccountUseCase.setMarketAccount(
            SetMarketAccountCommand(
                userId = getAdminUserId(),
                market = Market.BY_BIT,
                isProduction = false,
                displayName = "test",
                appKey = "key",
                appSecret = "secret",
            )
        ).id

        val savedStrategy = strategyRepository.save(
            StrategyEntity(
                id = 0,
                className = "SomethingStrategyClass",
                nameKo = "테스트",
                nameEn = "test",
            )
        )

        return Triple(marketAccountId, savedStrategy.className, savedStrategy.id)
    }

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