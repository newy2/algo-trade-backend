package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyCommandService
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.user_strategy.Product
import com.newy.algotrade.domain.user_strategy.SetUserStrategy
import com.newy.algotrade.domain.user_strategy.SetUserStrategyKey
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

private val incomingPortModel = SetUserStrategyCommand(
    marketAccountId = 1,
    strategyClassName = "BuyTripleRSIStrategy",
    productCategory = ProductCategory.USER_PICK,
    productType = ProductType.SPOT,
    productCodes = listOf("BTCUSDT", "ETHUSDT"),
    timeFrame = Candle.TimeFrame.M1,
)

@DisplayName("예외사항 테스트")
class UserStrategyCommandServiceExceptionTest {
    @Test
    fun `marketAccountId 가 없는 경우`() = runTest {
        val notFoundMarketAdapter = GetMarketPort { _ -> emptyList() }
        val service = newUserStrategyCommandService(
            getMarketPort = notFoundMarketAdapter,
        )

        try {
            service.setUserStrategy(incomingPortModel)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("marketAccountId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `strategyId 가 없는 경우`() = runTest {
        val hasNotStrategyAdapter = HasStrategyPort { _ -> false }
        val service = newUserStrategyCommandService(
            hasStrategyPort = hasNotStrategyAdapter,
        )

        try {
            service.setUserStrategy(incomingPortModel)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("strategyId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `이미 등록한 userStrategy 인 경우`() = runTest {
        val alreadySavedUserStrategyAdapter = HasUserStrategyPort { _ -> true }
        val service = newUserStrategyCommandService(
            hasUserStrategyPort = alreadySavedUserStrategyAdapter,
        )

        try {
            service.setUserStrategy(incomingPortModel)
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("이미 등록한 전략입니다.", e.message)
        }
    }

    @DisplayName("USER_PICK 예외사항 테스트")
    class UserPickUserStrategyCommandServiceExceptionTest {
        @Test
        fun `DB 에 저장되지 않은 productCode 를 입력한 경우`() = runTest {
            val notFoundProductAdapter = GetProductPort { _, _, _ -> emptyList() }
            val service = newUserStrategyCommandService(
                getProductPort = notFoundProductAdapter,
            )

            try {
                service.setUserStrategy(incomingPortModel)
                fail()
            } catch (e: NotFoundRowException) {
                assertEquals("productCode 를 찾을 수 없습니다. ([BTCUSDT, ETHUSDT])", e.message)
            }
        }

        @Test
        fun `DB 에 일부만 저장된 productCode 를 입력한 경우`() = runTest {
            val getOnlyBtcProductAdapter = GetProductPort { _, _, _ -> listOf(Product(1, "BTCUSDT")) }
            val service = newUserStrategyCommandService(
                getProductPort = getOnlyBtcProductAdapter,
            )

            try {
                service.setUserStrategy(incomingPortModel)
                fail()
            } catch (e: NotFoundRowException) {
                assertEquals("productCode 를 찾을 수 없습니다. ([ETHUSDT])", e.message)
            }
        }

        @Test
        fun `DB 에 저장된 productCode 와 일치하는 경우`() = runTest {
            val getFullProductAdapter = GetProductPort { _, _, _ ->
                listOf(Product(1, "BTCUSDT"), Product(2, "ETHUSDT"))
            }
            val service = newUserStrategyCommandService(
                getProductPort = getFullProductAdapter,
            )

            try {
                service.setUserStrategy(incomingPortModel)
            } catch (e: NotFoundRowException) {
                fail()
            }
        }
    }
}

@DisplayName("USER_PICK 등록하기")
class UserPickProductUserStrategyCommandServiceTest {
    @Test
    fun `setUserStrategy 성공시, CreateUserStrategyEvent 를 발행한다`() = runTest {
        var publishedEvent: CreateUserStrategyEvent? = null
        val eventBus = EventBus<CreateUserStrategyEvent>().also {
            it.addListener(coroutineContext) {
                publishedEvent = it
            }
            delay(1000) // wait for addListener
        }

        val createdUserStrategyId: Long = 10
        val service = newUserStrategyCommandService(
            eventBus = eventBus,
            setUserStrategyPort = { _ -> createdUserStrategyId }
        )

        service.setUserStrategy(incomingPortModel)

        coroutineContext.cancelChildren()
        assertEquals(CreateUserStrategyEvent(createdUserStrategyId), publishedEvent)
    }

    @Test
    fun `해피패스 port 메소드 실행순서`() = runTest {
        val methodCallLogs = mutableListOf<String>()
        val service = newUserStrategyCommandService(
            setUserStrategyPort = {
                1.toLong().also {
                    methodCallLogs.add("setUserStrategyPort")
                }
            },
            setUserStrategyProductPort = { _, _ ->
                methodCallLogs.add("setUserStrategyProductPort")
            },
            eventBus = object : EventBus<CreateUserStrategyEvent>() {
                override suspend fun publishEvent(event: CreateUserStrategyEvent) {
                    methodCallLogs.add("publishEvent")
                }
            }
        )

        service.setUserStrategy(incomingPortModel)

        assertEquals(
            listOf(
                "setUserStrategyPort",
                "setUserStrategyProductPort",
                "publishEvent"
            ),
            methodCallLogs
        )
    }
}

private fun newUserStrategyCommandService(
    getMarketPort: GetMarketPort = NoErrorGetMarketAdapter(),
    hasStrategyPort: HasStrategyPort = NoErrorHasStrategyAdapter(),
    getProductPort: GetProductPort = NoErrorGetProductAdapter(),
    hasUserStrategyPort: HasUserStrategyPort = NoErrorUserStrategyAdapter(),
    setUserStrategyPort: SetUserStrategyPort = NoErrorUserStrategyAdapter(),
    setUserStrategyProductPort: SetUserStrategyProductPort = NoErrorSetUserStrategyProductAdapter(),
    eventBus: EventBus<CreateUserStrategyEvent> = EventBus(),
) = UserStrategyCommandService(
    getMarketPort = getMarketPort,
    hasStrategyPort = hasStrategyPort,
    getProductPort = getProductPort,
    hasUserStrategyPort = hasUserStrategyPort,
    setUserStrategyPort = setUserStrategyPort,
    setUserStrategyProductPort = setUserStrategyProductPort,
    eventBus = eventBus,
)


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
    override suspend fun setUserStrategy(setUserStrategy: SetUserStrategy): Long = 1
    override suspend fun hasUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean = false
}

class NoErrorSetUserStrategyProductAdapter : SetUserStrategyProductPort {
    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean = true
}