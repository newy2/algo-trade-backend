package com.newy.algotrade.unit.user_strategy.application.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.user_strategy.application.service.SetUserStrategyService
import com.newy.algotrade.coroutine_based_application.user_strategy.domain.Product
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class NoErrorGetMarketAdapter : GetMarketPort {
    override suspend fun getMarketIdsBy(marketAccountId: Long): List<Long> {
        return listOf(1)
    }
}

class NoErrorHasStrategyAdapter : HasStrategyPort {
    override suspend fun hasStrategyByClassName(strategyClassName: String): Boolean {
        return true
    }
}

class NoErrorGetProductAdapter : GetProductPort {
    override suspend fun getProducts(
        marketIds: List<Long>,
        productType: ProductType,
        productCodes: List<String>
    ): List<Product> {
        return productCodes.mapIndexed { index, code ->
            Product((index + 1).toLong(), code)
        }
    }
}

open class NoErrorUserStrategyAdapter : UserStrategyPort {
    override suspend fun setUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType,
        productCategory: ProductCategory,
        timeFrame: Candle.TimeFrame,
    ): Long = 1

    override suspend fun hasUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType
    ): Boolean = false
}

class NoErrorSetUserStrategyProductAdapter : SetUserStrategyProductPort {
    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean = true
}

class SetUserStrategyServiceWrapper(
    marketPort: GetMarketPort = NoErrorGetMarketAdapter(),
    strategyPort: HasStrategyPort = NoErrorHasStrategyAdapter(),
    productPort: GetProductPort = NoErrorGetProductAdapter(),
    userStrategyPort: UserStrategyPort = NoErrorUserStrategyAdapter(),
    userStrategyProductPort: SetUserStrategyProductPort = NoErrorSetUserStrategyProductAdapter(),
    eventBus: EventBus<CreateUserStrategyEvent> = EventBus(),
) : SetUserStrategyService(
    marketPort = marketPort,
    strategyPort = strategyPort,
    productPort = productPort,
    userStrategyPort = userStrategyPort,
    userStrategyProductPort = userStrategyProductPort,
    eventBus = eventBus,
)

private val STRATEGY_COMMAND = SetUserStrategyCommand(
    marketAccountId = 1,
    strategyClassName = "BuyTripleRSIStrategy",
    productCategory = ProductCategory.USER_PICK,
    productType = ProductType.SPOT,
    productCodes = listOf("BTCUSDT", "ETHUSDT"),
    timeFrame = Candle.TimeFrame.M1,
)

@DisplayName("SetUserStrategyService 공통 제약사항 테스트")
class DefaultSetUserStrategyServiceTest {
    @Test
    fun `marketAccountId 가 없는 경우`() = runTest {
        class NotFoundGetMarketAdapter : GetMarketPort {
            override suspend fun getMarketIdsBy(marketAccountId: Long): List<Long> = emptyList()
        }

        val service = SetUserStrategyServiceWrapper(
            marketPort = NotFoundGetMarketAdapter(),
        )

        try {
            service.setUserStrategy(STRATEGY_COMMAND)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("marketAccountId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `strategyId 가 없는 경우`() = runTest {
        class HasNotStrategyAdapter : HasStrategyPort {
            override suspend fun hasStrategyByClassName(strategyClassName: String): Boolean = false
        }

        val service = SetUserStrategyServiceWrapper(
            strategyPort = HasNotStrategyAdapter(),
        )

        try {
            service.setUserStrategy(STRATEGY_COMMAND)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("strategyId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `이미 등록한 userStrategy 인 경우`() = runTest {
        class AlreadyRegisteredUserStrategyAdapter : NoErrorUserStrategyAdapter() {
            override suspend fun hasUserStrategy(
                marketServerAccountId: Long, strategyClassName: String, productType: ProductType
            ): Boolean {
                return true
            }
        }

        val service = SetUserStrategyServiceWrapper(
            userStrategyPort = AlreadyRegisteredUserStrategyAdapter(),
        )

        try {
            service.setUserStrategy(STRATEGY_COMMAND)
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("이미 등록한 전략입니다.", e.message)
        }
    }
}

@DisplayName("USER_PICK 제약사항 테스트")
class UserPickSetUserStrategyServiceTest {
    @Test
    fun `DB 에 저장되지 않은 productCode 를 입력한 경우`() = runTest {
        class GetProductSubListAdapter : GetProductPort {
            override suspend fun getProducts(
                marketIds: List<Long>,
                productType: ProductType,
                productCodes: List<String>
            ): List<Product> {
                return listOf(Product(1, "BTCUSDT"))
            }
        }

        val service = SetUserStrategyServiceWrapper(
            productPort = GetProductSubListAdapter(),
        )

        try {
            service.setUserStrategy(STRATEGY_COMMAND)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("productCode 를 찾을 수 없습니다. ([ETHUSDT])", e.message)
        }
    }
}

@DisplayName("USER_PICK 등록하기")
class UserPickProductSetUserStrategyServiceTest : NoErrorUserStrategyAdapter(), SetUserStrategyProductPort {
    private val createdUserStrategyId: Long = 10
    private lateinit var log: String
    private lateinit var eventBus: EventBus<CreateUserStrategyEvent>
    private lateinit var service: SetUserStrategyService

    @BeforeEach
    fun setUp() {
        log = ""
        eventBus = EventBus()
        service = SetUserStrategyServiceWrapper(
            userStrategyPort = this@UserPickProductSetUserStrategyServiceTest,
            userStrategyProductPort = this@UserPickProductSetUserStrategyServiceTest,
            eventBus = eventBus
        )
    }

    @Test
    fun `outgoing 포트 호출 확인`() = runTest {
        service.setUserStrategy(STRATEGY_COMMAND)

        assertEquals("setUserStrategy setUserStrategyProduct ", log)
    }

    @Test
    fun `이벤트 발행 확인`() = runTest {
        var publishedEvent: CreateUserStrategyEvent? = null
        eventBus.addListener(coroutineContext) {
            publishedEvent = it
        }

        service.setUserStrategy(STRATEGY_COMMAND)
        delay(2000)
        coroutineContext.cancelChildren()

        assertEquals(CreateUserStrategyEvent(createdUserStrategyId), publishedEvent)
    }

    override suspend fun setUserStrategy(
        marketAccountId: Long,
        strategyClassName: String,
        productType: ProductType,
        productCategory: ProductCategory,
        timeFrame: Candle.TimeFrame,
    ): Long {
        log += "setUserStrategy "
        return createdUserStrategyId
    }

    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean {
        log += "setUserStrategyProduct "
        return true
    }
}