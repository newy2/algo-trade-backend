package com.newy.algotrade.integration.run_strategy.adapter.out.persistence

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductCategory
import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.common.domain.exception.NotFoundRowException
import com.newy.algotrade.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.run_strategy.adapter.out.persistence.StrategySignalHistoryAdapter
import com.newy.algotrade.run_strategy.adapter.out.persistence.repository.MarketRepositoryForRunStrategy
import com.newy.algotrade.run_strategy.adapter.out.persistence.repository.ProductRepositoryForRunStrategy
import com.newy.algotrade.run_strategy.domain.StrategySignalHistoryKey
import com.newy.algotrade.strategy.adapter.out.persistence.repository.StrategyR2dbcEntity
import com.newy.algotrade.strategy.adapter.out.persistence.repository.StrategyRepository
import com.newy.algotrade.user_strategy.port.`in`.UserStrategyUseCase
import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand
import helpers.spring.BaseDbTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import java.time.OffsetDateTime

class ExceptionTest(
    @Autowired private val productRepository: ProductRepositoryForRunStrategy,
    @Autowired private val marketRepository: MarketRepositoryForRunStrategy,
    @Autowired private val adapter: StrategySignalHistoryAdapter
) : BaseStrategySignalHistoryAdapterTest() {
    @Test
    fun `market 테이블에서 데이터를 찾지 못하면 에러가 발생한다`() = runTransactional {
        val userStrategyId = setUserStrategyId()
        val historyKey = newStrategySignalHistoryKey(userStrategyId)

        marketRepository.deleteAll()
        try {
            adapter.findHistory(historyKey)
        } catch (e: NotFoundRowException) {
            assertEquals("product 를 찾을 수 없습니다. (market: BY_BIT, productType: SPOT, productCode: BTCUSDT)", e.message)
        }
    }

    @Test
    fun `product 테이블에서 데이터를 찾지 못하면 에러가 발생한다`() = runTransactional {
        val userStrategyId = setUserStrategyId()
        val historyKey = newStrategySignalHistoryKey(userStrategyId)

        productRepository.deleteAll()
        try {
            adapter.findHistory(historyKey)
        } catch (e: NotFoundRowException) {
            assertEquals("product 를 찾을 수 없습니다. (market: BY_BIT, productType: SPOT, productCode: BTCUSDT)", e.message)
        }
    }
}

@DisplayName("히스토리 저장하기 테스트")
class AddHistoryTest(
    @Autowired private val adapter: StrategySignalHistoryAdapter
) : BaseStrategySignalHistoryAdapterTest() {
    @Test
    fun `히스토리 저장하기`() = runTransactional {
        val userStrategyId = setUserStrategyId()
        val historyKey = newStrategySignalHistoryKey(userStrategyId)
        assertTrue(adapter.findHistory(historyKey).isEmpty())

        adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 1000))

        assertEquals(
            listOf(newStrategySignal(OrderType.BUY, price = 1000)),
            adapter.findHistory(historyKey).strategySignals()
        )
    }
}

class StrategySignalHistoryAdapterTest(
    @Autowired private val adapter: StrategySignalHistoryAdapter
) : BaseStrategySignalHistoryAdapterTest() {
    @Test
    fun `저장된 히스토리가 없는 경우`() = runTransactional {
        val userStrategyId = setUserStrategyId()
        val historyKey = newStrategySignalHistoryKey(userStrategyId)

        assertTrue(adapter.findHistory(historyKey).isEmpty())
    }

    @Test
    fun `히스토리 저장하기`() = runTransactional {
        val userStrategyId = setUserStrategyId(entryType = OrderType.BUY)
        val historyKey = newStrategySignalHistoryKey(userStrategyId)

        adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 1000))

        assertEquals(
            listOf(newStrategySignal(OrderType.BUY, price = 1000)),
            adapter.findHistory(historyKey, maxSize = 1).strategySignals()
        )
    }
}

@DisplayName("getHistory 메소드 maxSize 테스트")
class GetHistoryFilteringTest(
    @Autowired private val adapter: StrategySignalHistoryAdapter
) : BaseStrategySignalHistoryAdapterTest() {
    @Test
    fun `BUY entryType 히스토리 - DB 에 저장된 마지막 주문부터 maxSize 까지 데이터를 가져오고, 첫 번째 element 의 orderType 은 entryType 과 같아야 한다`() =
        runTransactional {
            val userStrategyId = setUserStrategyId(entryType = OrderType.BUY)
            val historyKey = newStrategySignalHistoryKey(userStrategyId)

            adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 1000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.SELL, price = 2000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 3000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.SELL, price = 4000))

            assertEquals(
                emptyList<StrategySignal>(),
                adapter.findHistory(historyKey, maxSize = 1).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.BUY, price = 3000),
                    newStrategySignal(OrderType.SELL, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 2).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.BUY, price = 3000),
                    newStrategySignal(OrderType.SELL, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 3).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.BUY, price = 1000),
                    newStrategySignal(OrderType.SELL, price = 2000),
                    newStrategySignal(OrderType.BUY, price = 3000),
                    newStrategySignal(OrderType.SELL, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 4).strategySignals()
            )
        }

    @Test
    fun `SELL entryType 히스토리 - DB 에 저장된 마지막 주문부터 maxSize 까지 데이터를 가져오고, 첫 번째 element 의 orderType 은 entryType 과 같아야 한다`() =
        runTransactional {
            val userStrategyId = setUserStrategyId(entryType = OrderType.SELL)
            val historyKey = newStrategySignalHistoryKey(userStrategyId)

            adapter.saveHistory(historyKey, newStrategySignal(OrderType.SELL, price = 1000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 2000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.SELL, price = 3000))
            adapter.saveHistory(historyKey, newStrategySignal(OrderType.BUY, price = 4000))

            assertEquals(
                emptyList<StrategySignal>(),
                adapter.findHistory(historyKey, maxSize = 1).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.SELL, price = 3000),
                    newStrategySignal(OrderType.BUY, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 2).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.SELL, price = 3000),
                    newStrategySignal(OrderType.BUY, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 3).strategySignals()
            )
            assertEquals(
                listOf(
                    newStrategySignal(OrderType.SELL, price = 1000),
                    newStrategySignal(OrderType.BUY, price = 2000),
                    newStrategySignal(OrderType.SELL, price = 3000),
                    newStrategySignal(OrderType.BUY, price = 4000),
                ),
                adapter.findHistory(historyKey, maxSize = 4).strategySignals()
            )
        }
}

open class BaseStrategySignalHistoryAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var strategyRepository: StrategyRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var marketAccountUseCase: SetMarketAccountUseCase

    @Autowired
    private lateinit var userStrategyUseCase: UserStrategyUseCase

    protected suspend fun setUserStrategyId(entryType: OrderType = OrderType.BUY): Long {
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


        strategyRepository.save(
            StrategyR2dbcEntity(
                className = "SomethingStrategyClass",
                entryType = entryType,
                nameKo = "테스트",
                nameEn = "test",
            )
        )

        return userStrategyUseCase.setUserStrategy(
            SetUserStrategyCommand(
                marketAccountId = marketAccountId,
                strategyClassName = "SomethingStrategyClass",
                productCategory = ProductCategory.USER_PICK,
                productType = ProductType.SPOT,
                productCodes = listOf("BTCUSDT", "ETHUSDT"),
                timeFrame = Candle.TimeFrame.M1,
            )
        )
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

    protected fun newStrategySignalHistoryKey(userStrategyId: Long) =
        StrategySignalHistoryKey(
            userStrategyId = userStrategyId,
            productPriceKey = ProductPriceKey(
                market = Market.BY_BIT,
                productType = ProductType.SPOT,
                productCode = "BTCUSDT",
                interval = Candle.TimeFrame.M1.timePeriod,
            )
        )

    protected fun newStrategySignal(orderType: OrderType, price: Int) =
        StrategySignal(
            orderType = orderType,
            timeFrame = Candle.TimeRange(
                period = Candle.TimeFrame.M1.timePeriod,
                begin = OffsetDateTime.parse("2024-05-01T00:00Z"),
            ),
            price = price.toBigDecimal(),
        )
}