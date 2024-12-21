package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.Strategy
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.`in`.GetCandlesQuery
import com.newy.algotrade.product_price.port.out.*
import com.newy.algotrade.run_strategy.domain.RunStrategyResult
import com.newy.algotrade.run_strategy.domain.StrategySignalHistoryKey
import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategySignalHistoryStoreAdapter
import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.run_strategy.port.out.*
import com.newy.algotrade.run_strategy.service.RunStrategyCommandService
import helpers.BooleanRule
import helpers.productPrice
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

private val beginTime = OffsetDateTime.parse("2024-05-01T00:00Z")

class LongPositionStrategy(entry: Boolean, exit: Boolean) : Strategy(
    entryType = OrderType.BUY,
    entryRule = BooleanRule(entry),
    exitRule = BooleanRule(exit),
)

class ShortPositionStrategy(entry: Boolean, exit: Boolean) : Strategy(
    entryType = OrderType.SELL,
    entryRule = BooleanRule(entry),
    exitRule = BooleanRule(exit),
)

private val BTC_1MINUTE = productPriceKey(productCode = "BTCUSDT", interval = Duration.ofMinutes(1))

@DisplayName("strategy 가 실행되지 않는 조건 테스트")
class EmptyRunStrategyCommandServiceTest {
    private val productPriceKey = productPriceKey(productCode = "BTCUSDT")
    private val expectedResult = RunStrategyResult(
        totalStrategyCount = 0,
        noneSignalCount = 0,
        buySignalCount = 0,
        sellSignalCount = 0,
    )

    @Test
    fun `candle 데이터가 없으면 실행되지 않는다`() = runTest {
        val notFoundCandlesQuery = GetCandlesQuery { DEFAULT_CHART_FACTORY.candles() }
        val service = newRunStrategyCommandService(
            getCandlesQuery = notFoundCandlesQuery,
        )

        val result = service.runStrategy(productPriceKey)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `strategy 를 찾을 수 없으면 실행되지 않는다`() = runTest {
        val notFoundRunnableStrategyAdapter = FilterStrategyPort { emptyMap() }
        val service = newRunStrategyCommandService(
            filterStrategyPort = notFoundRunnableStrategyAdapter
        )

        val result = service.runStrategy(productPriceKey)

        assertEquals(expectedResult, result)
    }
}

@DisplayName("StrategySignalHistory 를 사용한 테스트 - 진입 이력이 있는 경우와 없는 경우")
class RunStrategyCommandServiceWithStrategySignalHistoryTest {
    private val productPriceKey = productPriceKey(productCode = "BTCUSDT")
    private val getTestDataAdapter = FilterStrategyPort {
        mapOf(
            userStrategyKey(1, BTC_1MINUTE) to LongPositionStrategy(entry = true, exit = true),
            userStrategyKey(2, BTC_1MINUTE) to LongPositionStrategy(entry = true, exit = false),
            userStrategyKey(3, BTC_1MINUTE) to LongPositionStrategy(entry = false, exit = false),
        )
    }
    private val enteredSignalHistoryAdapter = object : FindStrategySignalHistoryPort {
        override suspend fun findHistory(key: StrategySignalHistoryKey, maxSize: Int): StrategySignalHistory {
            return StrategySignalHistory().also {
                it.add(
                    StrategySignal(
                        orderType = OrderType.BUY,
                        timeFrame = Candle.TimeRange(
                            period = Duration.ofMinutes(1),
                            begin = beginTime
                        ),
                        price = 1000.toBigDecimal(),
                    )
                )
            }
        }
    }

    @Test
    fun `entry 주문 히스토리가 없는 경우`() = runTest {
        val service = newRunStrategyCommandService(
            filterStrategyPort = getTestDataAdapter,
        )

        val result = service.runStrategy(productPriceKey)

        assertEquals(
            RunStrategyResult(
                totalStrategyCount = 3,
                noneSignalCount = 1,
                buySignalCount = 2,
                sellSignalCount = 0,
            ),
            result
        )
    }

    @Test
    fun `entry 주문 히스토리가 있는 경우`() = runTest {
        val service = newRunStrategyCommandService(
            filterStrategyPort = getTestDataAdapter,
            findStrategySignalHistoryPort = enteredSignalHistoryAdapter,
        )

        val result = service.runStrategy(productPriceKey)

        assertEquals(
            RunStrategyResult(
                totalStrategyCount = 3,
                noneSignalCount = 2,
                buySignalCount = 0,
                sellSignalCount = 1,
            ),
            result
        )
    }
}

@DisplayName("strategy 에 여러 포지션이 섞인 경우에 대한 테스트")
class MixedPositionRunStrategyCommandServiceTest {
    @Test
    fun `여러 position 전략이 섞인 경우`() = runTest {
        val productPriceKey = productPriceKey(productCode = "BTCUSDT")
        val getTestDataAdapter = FilterStrategyPort {
            mapOf(
                userStrategyKey(1, BTC_1MINUTE) to LongPositionStrategy(entry = true, exit = true),
                userStrategyKey(2, BTC_1MINUTE) to ShortPositionStrategy(entry = true, exit = true),
                userStrategyKey(3, BTC_1MINUTE) to LongPositionStrategy(entry = false, exit = false),
            )
        }
        val service = newRunStrategyCommandService(
            filterStrategyPort = getTestDataAdapter,
        )

        val result = service.runStrategy(productPriceKey)

        assertEquals(
            RunStrategyResult(
                totalStrategyCount = 3,
                noneSignalCount = 1,
                buySignalCount = 1,
                sellSignalCount = 1,
            ),
            result
        )
    }
}

@DisplayName("port 호출 확인 테스트")
class RunStrategyCommandServiceTest {
    private val productPriceKey = productPriceKey(productCode = "BTCUSDT")
    private val getTestDataAdapter = FilterStrategyPort {
        mapOf(
            userStrategyKey(1, BTC_1MINUTE) to LongPositionStrategy(entry = true, exit = true),
            userStrategyKey(2, BTC_1MINUTE) to ShortPositionStrategy(entry = true, exit = false),
            userStrategyKey(3, BTC_1MINUTE) to LongPositionStrategy(entry = false, exit = false),
        )
    }

    @Test
    fun `strategy signal 발생시 호출하는 port 확인`() = runTest {
        val logs = mutableListOf<String>()

        val service = newRunStrategyCommandService(
            filterStrategyPort = getTestDataAdapter,
            saveStrategySignalHistoryPort = { strategySignalHistoryKey, signal ->
                logs.add("addStrategySignalHistoryPort(id: ${strategySignalHistoryKey.userStrategyId}, signal: ${signal.orderType})")
            },
            onCreatedStrategySignalPort = { userStrategyId, signal ->
                logs.add("onCreatedStrategySignalPort(id: $userStrategyId, signal: ${signal.orderType})")
            }
        )

        service.runStrategy(productPriceKey)

        assertEquals(
            listOf(
                "addStrategySignalHistoryPort(id: 1, signal: BUY)",
                "onCreatedStrategySignalPort(id: 1, signal: BUY)",
                "addStrategySignalHistoryPort(id: 2, signal: SELL)",
                "onCreatedStrategySignalPort(id: 2, signal: SELL)",
            ),
            logs
        )
    }
}

fun newRunStrategyCommandService(
    strategySignalHistoryPort: StrategySignalHistoryPort = InMemoryStrategySignalHistoryStoreAdapter(),

    getCandlesQuery: GetCandlesQuery = NoErrorGetCandlesQueryService(),
    filterStrategyPort: FilterStrategyPort = InMemoryStrategyStoreAdapter(),
    findStrategySignalHistoryPort: FindStrategySignalHistoryPort = strategySignalHistoryPort,
    saveStrategySignalHistoryPort: SaveStrategySignalHistoryPort = strategySignalHistoryPort,
    onCreatedStrategySignalPort: OnCreatedStrategySignalPort = NoErrorOnCreatedStrategySignalAdapter(),
) = RunStrategyCommandService(
    getCandlesQuery = getCandlesQuery,
    filterStrategyPort = filterStrategyPort,
    findStrategySignalHistoryPort = findStrategySignalHistoryPort,
    saveStrategySignalHistoryPort = saveStrategySignalHistoryPort,
    onCreatedStrategySignalPort = onCreatedStrategySignalPort,
)

class NoErrorGetCandlesQueryService(
) : GetCandlesQuery {
    override fun getCandles(key: ProductPriceKey): Candles =
        DEFAULT_CHART_FACTORY.candles().also {
            it.upsert(productPrice(amount = 1000, interval = Duration.ofMinutes(1)))
        }
}

class NoErrorOnCreatedStrategySignalAdapter : OnCreatedStrategySignalPort {
    override suspend fun onCreatedSignal(userStrategyId: Long, signal: StrategySignal) {
    }
}