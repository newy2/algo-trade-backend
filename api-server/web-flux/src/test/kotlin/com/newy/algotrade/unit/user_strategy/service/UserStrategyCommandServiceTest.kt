package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.consts.ProductCategory
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateUserStrategyEvent
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.strategy.port.`in`.HasStrategyQuery
import com.newy.algotrade.user_strategy.domain.Product
import com.newy.algotrade.user_strategy.domain.SetUserStrategy
import com.newy.algotrade.user_strategy.domain.SetUserStrategyKey
import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.user_strategy.port.out.*
import com.newy.algotrade.user_strategy.service.UserStrategyCommandService
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
        val notFoundMarketAdapter = FindMarketPort { emptyList() }
        val service = newUserStrategyCommandService(
            findMarketPort = notFoundMarketAdapter,
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
        val hasNotStrategyService = HasStrategyQuery { false }
        val service = newUserStrategyCommandService(
            hasStrategyQuery = hasNotStrategyService,
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
        val alreadySavedUserStrategyAdapter = ExistsUserStrategyPort { true }
        val service = newUserStrategyCommandService(
            existsUserStrategyPort = alreadySavedUserStrategyAdapter,
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
            val notFoundProductAdapter = FindProductPort { _, _, _ -> emptyList() }
            val service = newUserStrategyCommandService(
                findProductPort = notFoundProductAdapter,
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
            val getOnlyBtcProductAdapter = FindProductPort { _, _, _ -> listOf(Product(1, "BTCUSDT")) }
            val service = newUserStrategyCommandService(
                findProductPort = getOnlyBtcProductAdapter,
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
            val getFullProductAdapter = FindProductPort { _, _, _ ->
                listOf(Product(1, "BTCUSDT"), Product(2, "ETHUSDT"))
            }
            val service = newUserStrategyCommandService(
                findProductPort = getFullProductAdapter,
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
            saveUserStrategyPort = { createdUserStrategyId }
        )

        service.setUserStrategy(incomingPortModel)

        coroutineContext.cancelChildren()
        assertEquals(CreateUserStrategyEvent(createdUserStrategyId), publishedEvent)
    }

    @Test
    fun `해피패스 port 메소드 실행순서`() = runTest {
        val methodCallLogs = mutableListOf<String>()
        val service = newUserStrategyCommandService(
            saveUserStrategyPort = {
                1.toLong().also {
                    methodCallLogs.add("setUserStrategyPort")
                }
            },
            saveAllUserStrategyProductPort = { _, _ ->
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
    hasStrategyQuery: HasStrategyQuery = NoErrorHasStrategyService(),
    findMarketPort: FindMarketPort = NoErrorFindMarketAdapter(),
    findProductPort: FindProductPort = NoErrorFindProductAdapter(),
    existsUserStrategyPort: ExistsUserStrategyPort = NoErrorUserStrategyAdapter(),
    saveUserStrategyPort: SaveUserStrategyPort = NoErrorUserStrategyAdapter(),
    saveAllUserStrategyProductPort: SaveAllUserStrategyProductPort = NoErrorSaveAllUserStrategyProductAdapter(),
    eventBus: EventBus<CreateUserStrategyEvent> = EventBus(),
) = UserStrategyCommandService(
    hasStrategyQuery = hasStrategyQuery,
    findMarketPort = findMarketPort,
    findProductPort = findProductPort,
    existsUserStrategyPort = existsUserStrategyPort,
    saveUserStrategyPort = saveUserStrategyPort,
    saveAllUserStrategyProductPort = saveAllUserStrategyProductPort,
    eventBus = eventBus,
)


class NoErrorHasStrategyService : HasStrategyQuery {
    override suspend fun hasStrategy(className: String): Boolean {
        return true
    }
}

class NoErrorFindMarketAdapter : FindMarketPort {
    override suspend fun findMarketIdsBy(marketAccountId: Long): List<Long> {
        return listOf(1)
    }
}

class NoErrorFindProductAdapter : FindProductPort {
    override suspend fun findProducts(
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
    override suspend fun saveUserStrategy(setUserStrategy: SetUserStrategy): Long = 1
    override suspend fun existsUserStrategy(setUserStrategyKey: SetUserStrategyKey): Boolean = false
}

class NoErrorSaveAllUserStrategyProductAdapter : SaveAllUserStrategyProductPort {
    override suspend fun saveAllUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean = true
}