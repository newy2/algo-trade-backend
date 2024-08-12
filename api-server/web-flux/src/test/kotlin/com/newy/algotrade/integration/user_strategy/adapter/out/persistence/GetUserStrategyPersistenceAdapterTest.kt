package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import com.newy.algotrade.web_flux.strategy.adapter.out.persistence.repository.StrategyR2dbcEntity
import com.newy.algotrade.web_flux.strategy.adapter.out.persistence.repository.StrategyRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.UserStrategyProductPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.*
import helpers.BaseDbTest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

@DisplayName("getAllUserStrategyKeys 테스트")
class GetAllUserStrategyKeysTest : BaseUserStrategyQueryPersistenceAdapterTest() {
    @Test
    fun `user strategy 가 1개인 경우`() = runTransactional {
        val userStrategyId = setInitData(strategyClassName = "A", listOf("BTCUSDT"))

        val actualList = adapter.getAllUserStrategyKeys()
        val expectedList = listOf(
            UserStrategyKey(
                userStrategyId = userStrategyId,
                strategyClassName = "A",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "BTCUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            ),
        )

        assertEquals(expectedList, actualList)
    }

    @Test
    fun `user strategy 가 2개 이상인 경우`() = runTransactional {
        val userStrategyId1 = setInitData(strategyClassName = "A", listOf("BTCUSDT", "ETHUSDT"))
        val userStrategyId2 = setInitData(strategyClassName = "B", listOf("SOLUSDT"))

        val actualList = adapter.getAllUserStrategyKeys()
        val expectedList = listOf(
            UserStrategyKey(
                userStrategyId = userStrategyId1,
                strategyClassName = "A",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "BTCUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            ),
            UserStrategyKey(
                userStrategyId = userStrategyId1,
                strategyClassName = "A",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "ETHUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            ),
            UserStrategyKey(
                userStrategyId = userStrategyId2,
                strategyClassName = "B",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "SOLUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            )
        )

        assertEquals(expectedList, actualList)
    }
}

@DisplayName("getUserStrategyKeys 테스트")
class GetUserStrategyKeysTest : BaseUserStrategyQueryPersistenceAdapterTest() {
    @Test
    fun `ID 가 없는 경우`() = runTransactional {
        val unRegisteredId: Long = 100

        assertTrue(adapter.getUserStrategyKeys(unRegisteredId).isEmpty())
    }

    @Test
    fun `ID 가 있는 경우`() = runTransactional {
        val userStrategyId = setInitData(strategyClassName = "A", listOf("BTCUSDT", "ETHUSDT"))

        val actual = adapter.getUserStrategyKeys(userStrategyId)
        val expected = listOf(
            UserStrategyKey(
                userStrategyId = userStrategyId,
                strategyClassName = "A",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "BTCUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            ),
            UserStrategyKey(
                userStrategyId = userStrategyId,
                strategyClassName = "A",
                productPriceKey = ProductPriceKey(
                    market = Market.BY_BIT,
                    productType = ProductType.SPOT,
                    productCode = "ETHUSDT",
                    interval = Candle.TimeFrame.M1.timePeriod,
                ),
            )
        )


        assertEquals(expected, actual)
    }
}

open class BaseUserStrategyQueryPersistenceAdapterTest : BaseDbTest() {
    @Autowired
    protected lateinit var adapter: UserStrategyProductPersistenceAdapter

    @Autowired
    private lateinit var marketRepository: MarketRepositoryForStrategy

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var strategyRepository: StrategyRepository

    @Autowired
    private lateinit var userStrategyRepository: UserStrategyRepository

    @Autowired
    private lateinit var userStrategyProductRepository: UserStrategyProductRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var marketAccountUseCase: SetMarketAccountUseCase

    private var index = 0

    @BeforeEach
    fun empty() = runBlocking {
        val list = adapter.getAllUserStrategyKeys()
        assertEquals(0, list.size)
    }

    protected suspend fun setInitData(strategyClassName: String, productCodes: List<String>): Long {
        val nextIndex = index++
        val marketAccountId = marketAccountUseCase.setMarketAccount(
            SetMarketAccountCommand(
                userId = getAdminUserId(),
                market = Market.BY_BIT,
                isProduction = false,
                displayName = "test",
                appKey = "key$nextIndex",
                appSecret = "secret$nextIndex",
            )
        ).id

        val strategyId = strategyRepository.save(
            StrategyR2dbcEntity(
                className = strategyClassName,
                entryType = OrderType.BUY,
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

        userStrategyProductRepository.saveAll(
            productIds.mapIndexed { index, productId ->
                UserStrategyProductR2dbcEntity(
                    userStrategyId = userStrategyId,
                    productId = productId,
                    sort = index + 1
                )
            }
        ).collect()

        return userStrategyId
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