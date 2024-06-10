package com.newy.algotrade.unit.product.application.service.strategy

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryStrategyStore
import com.newy.algotrade.coroutine_based_application.product.application.service.strategy.RunStrategyService
import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.*
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import helpers.BooleanRule
import helpers.productPrice
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

private val now = OffsetDateTime.parse("2024-05-01T00:00Z")

class BooleanStrategy(entry: Boolean, exit: Boolean) : Strategy(
    OrderType.BUY,
    BooleanRule(entry),
    BooleanRule(exit),
) {
    override fun version() = "0"
}

private val BTC_1MINUTE = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
private val ETH_1MINUTE = productPriceKey("ETHUSDT", Duration.ofMinutes(1))

@DisplayName("전략 실행하기 테스트")
class RunStrategyServiceTest : OnCreatedStrategySignalPort {
    private lateinit var service: RunStrategyUseCase
    private lateinit var strategySignalHistoryPort: StrategySignalHistoryPort
    private lateinit var results: MutableMap<String, StrategySignal>

    override suspend fun onCreatedSignal(userStrategyId: String, orderSignal: StrategySignal) {
        results[userStrategyId] = orderSignal
    }

    @BeforeEach
    fun setUp() {
        strategySignalHistoryPort = InMemoryStrategySignalHistoryStore()
        service = RunStrategyService(
            candlePort = InMemoryCandleStore().also {
                it.setCandles(
                    BTC_1MINUTE, listOf(
                        productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0)),
                        productPrice(2000, Duration.ofMinutes(1), now.plusMinutes(1)),
                    )
                )
                it.setCandles(
                    ETH_1MINUTE, listOf(
                        productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0)),
                    )
                )
            },
            strategyPort = InMemoryStrategyStore().also {
                it.addStrategy(userStrategyKey("id1", BTC_1MINUTE), BooleanStrategy(entry = true, exit = true))
                it.addStrategy(userStrategyKey("id2", BTC_1MINUTE), BooleanStrategy(entry = false, exit = false))
                it.addStrategy(userStrategyKey("id3", ETH_1MINUTE), BooleanStrategy(entry = true, exit = false))
            },
            strategySignalHistoryPort = strategySignalHistoryPort,
            onCreatedStrategySignalPort = this
        )
        results = mutableMapOf()
    }

    @Test
    fun `BTC 상품코드로 실행`() = runTest {
        service.runStrategy(BTC_1MINUTE)

        val lastPrice = productPrice(2000, Duration.ofMinutes(1), now.plusMinutes(1))
        val signal = StrategySignal(OrderType.BUY, lastPrice.time, lastPrice.price.close)

        assertEquals(mapOf("id1" to signal), results, "OrderSignal 은 Candles#lastCandle 값으로 생성되야 한다")
        strategySignalHistoryPort.getHistory("id1").let {
            assertEquals(1, it.strategySignals().size)
            assertEquals(signal, it.lastStrategySignal())
        }
    }

    @Test
    fun `ETH 상품 코드로 실행`() = runTest {
        service.runStrategy(ETH_1MINUTE)

        val lastPrice = productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0))
        val signal = StrategySignal(OrderType.BUY, lastPrice.time, lastPrice.price.close)

        assertEquals(mapOf("id3" to signal), results, "OrderSignal 은 Candles#lastCandle 값으로 생성되야 한다")
        strategySignalHistoryPort.getHistory("id3").let {
            assertEquals(1, it.strategySignals().size)
            assertEquals(signal, it.lastStrategySignal())
        }
    }
}