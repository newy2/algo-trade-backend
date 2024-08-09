package com.newy.algotrade.unit.strategy.service

import com.newy.algotrade.coroutine_based_application.strategy.port.out.ExistsStrategyPort
import com.newy.algotrade.coroutine_based_application.strategy.port.out.FindAllStrategiesPort
import com.newy.algotrade.coroutine_based_application.strategy.port.out.FindStrategyPort
import com.newy.algotrade.coroutine_based_application.strategy.service.StrategyQueryService
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.common.exception.InitializedError
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.strategy.Strategy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class CheckRegisteredStrategyClassesTest {
    @Test
    fun `아무런 문제가 없는 경우`() = runTest {
        val getStrategyAdapter = FindAllStrategiesPort {
            listOf(
                Strategy(id = 1, className = "BuyTripleRSIStrategy", entryType = OrderType.BUY)
            )
        }
        val service = newStrategyQueryService(findAllStrategiesPort = getStrategyAdapter)

        service.checkRegisteredStrategyClasses()
    }

    @Test
    fun `Strategy 의 entryType 이 다르면 에러가 발생한다`() = runTest {
        val getNotEqualsEntryTypeAdapter = FindAllStrategiesPort {
            listOf(
                Strategy(id = 1, className = "BuyTripleRSIStrategy", entryType = OrderType.SELL)
            )
        }
        val service = newStrategyQueryService(findAllStrategiesPort = getNotEqualsEntryTypeAdapter)

        try {
            service.checkRegisteredStrategyClasses()
            fail()
        } catch (e: InitializedError) {
            assertEquals("entryType 이 다릅니다. (expected: SELL, actual: BUY)", e.message)
        }
    }

    @Test
    fun `className 으로 Strategy 클래스를 찾을 수 없으면 에러가 발생한다`() = runTest {
        val getNotFoundStrategyClassNameAdapter = FindAllStrategiesPort {
            listOf(
                Strategy(id = 1, className = "NotStrategyClassName", entryType = OrderType.SELL)
            )
        }
        val service = newStrategyQueryService(findAllStrategiesPort = getNotFoundStrategyClassNameAdapter)

        try {
            service.checkRegisteredStrategyClasses()
            fail()
        } catch (e: InitializedError) {
            assertEquals("Strategy 클래스를 찾을 수 없습니다. (className: NotStrategyClassName)", e.message)
        }
    }
}

class GetStrategyTest {
    @Test
    fun `className 으로 strategy 영속성 데이터를 찾을 수 없으면 에러가 발생한다`() = runTest {
        val notFoundStrategyAdapter = FindStrategyPort { null }
        val service = newStrategyQueryService(findStrategyPort = notFoundStrategyAdapter)

        try {
            service.getStrategyId("NotStrategyClassName")
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("strategy 를 찾을 수 없습니다. (className: NotStrategyClassName)", e.message)
        }
    }
}

class HasStrategyTest {
    @Test
    fun `className 으로 strategy 등록 여부를 확인한다`() = runTest {
        val hasStrategyAdapter = ExistsStrategyPort { it == "BuyTripleRSIStrategy" }
        val service = newStrategyQueryService(existsStrategyPort = hasStrategyAdapter)

        assertTrue(service.hasStrategy("BuyTripleRSIStrategy"))
        assertFalse(service.hasStrategy("NotStrategyClassName"))
    }
}

fun newStrategyQueryService(
    findAllStrategiesPort: FindAllStrategiesPort = FindAllStrategiesPort { emptyList() },
    findStrategyPort: FindStrategyPort = FindStrategyPort { null },
    existsStrategyPort: ExistsStrategyPort = ExistsStrategyPort { false },
) = StrategyQueryService(
    findAllStrategiesPort = findAllStrategiesPort,
    findStrategyPort = findStrategyPort,
    existsStrategyPort = existsStrategyPort,
)