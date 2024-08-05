package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.UserStrategyProductPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.*
import helpers.BaseDbTest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

@DisplayName("setUserStrategyProducts 테스트")
class SetUserStrategyProductsTest(
    @Autowired private val marketRepository: MarketRepositoryForStrategy,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val userStrategyProductRepository: UserStrategyProductRepository,
    @Autowired private val adapter: UserStrategyProductPersistenceAdapter,
) : BaseUserStrategyCommandProductPersistenceAdapterTest() {
    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val marketId = marketRepository.findByCode("BY_BIT")!!.id
        val products = productRepository.findAll().toList()
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "BTCUSDT" })
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "ETHUSDT" })
        assertFalse(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "XXXUSDT" })
    }

    @Test
    fun `strategy product 등록하기`() = runTransactional {
        val (userStrategyId, productIds) = setInitData(productCodes = listOf("BTCUSDT", "ETHUSDT"))

        val isSaved = adapter.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = productIds
        )
        val userStrategyProducts = userStrategyProductRepository.findAll().toList()

        assertTrue(isSaved)
        assertEquals(2, userStrategyProducts.size)
        assertEquals(
            UserStrategyProductR2dbcEntity(
                id = userStrategyProducts[0].id,
                userStrategyId = userStrategyId,
                productId = productIds[0],
                sort = 1
            ),
            userStrategyProducts[0]
        )
        assertEquals(
            UserStrategyProductR2dbcEntity(
                id = userStrategyProducts[1].id,
                userStrategyId = userStrategyId,
                productId = productIds[1],
                sort = 2
            ),
            userStrategyProducts[1]
        )
    }

    @Test
    fun `strategy product 가 없는 경우`() = runTransactional {
        val (userStrategyId, emptyProductIds) = setInitData(productCodes = emptyList())

        val isSaved = adapter.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = emptyProductIds
        )
        val products = userStrategyProductRepository.findAll().toList()

        assertFalse(isSaved)
        assertEquals(0, products.size)
    }
}

open class BaseUserStrategyCommandProductPersistenceAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var marketRepository: MarketRepositoryForStrategy

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var strategyRepository: StrategyRepository

    @Autowired
    private lateinit var userStrategyRepository: UserStrategyRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var marketAccountUseCase: SetMarketAccountUseCase

    protected suspend fun setInitData(productCodes: List<String>): Pair<Long, List<Long>> {
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

        val strategyId = strategyRepository.save(
            StrategyR2dbcEntity(
                id = 0,
                className = "SomethingStrategyClass",
                nameKo = "테스트",
                nameEn = "test",
            )
        ).let { it.id }

        val userStrategyId = userStrategyRepository.save(
            UserStrategyR2dbcEntity(
                marketAccountId = marketAccountId,
                strategyId = strategyId,
                productType = ProductType.SPOT.name,
                productCategory = ProductCategory.USER_PICK.name,
                timeFrame = Candle.TimeFrame.M1.name,
            )
        ).id

        val marketId = marketRepository.findByCode("BY_BIT")!!.id
        val productIds = productRepository.findAll()
            .filter { it.marketId == marketId && it.type == "SPOT" && productCodes.contains(it.code) }
            .map { it.id }
            .toList()

        return Pair(userStrategyId, productIds)
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