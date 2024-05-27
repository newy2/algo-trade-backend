package com.newy.algotrade.unit.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryStrategyStore
import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryUserStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.price2.application.service.RunUserStrategyService
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.*
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.BooleanRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

private val now = OffsetDateTime.parse("2024-05-01T00:00Z")

// TODO 테스트 헬퍼 메소드로 옮기자
private fun productPrice(amount: Int, interval: Duration, beginTime: OffsetDateTime = now) =
    Candle.TimeFrame.from(interval)!!(
        beginTime,
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        0.toBigDecimal(),
    )

private fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)

private fun userStrategyKey(userStrategyId: String, productPriceKey: ProductPriceKey) =
    UserStrategyKey(
        userStrategyId,
        StrategyId.BuyTripleRSIStrategy,
        productPriceKey
    )

class BooleanStrategy(entry: Boolean, exit: Boolean) : Strategy(
    OrderType.BUY,
    BooleanRule(entry),
    BooleanRule(exit),
) {
    override fun version() = "0"
}

private val BTC_1MINUTE = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
private val ETH_1MINUTE = productPriceKey("ETHUSDT", Duration.ofMinutes(1))

@DisplayName("사용자 전략 실행하기 테스트")
class RunUserStrategyServiceTest : OnCreateUserStrategySignalPort {
    private lateinit var service: RunUserStrategyUseCase
    private lateinit var results: MutableMap<String, OrderSignal>

    override fun onCreateSignal(userStrategyId: String, orderSignal: OrderSignal) {
        results[userStrategyId] = orderSignal
    }

    @BeforeEach
    fun setUp() {
        service = RunUserStrategyService(
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
                it.add(userStrategyKey("id1", BTC_1MINUTE), BooleanStrategy(entry = true, exit = true))
                it.add(userStrategyKey("id2", BTC_1MINUTE), BooleanStrategy(entry = false, exit = false))
                it.add(userStrategyKey("id3", ETH_1MINUTE), BooleanStrategy(entry = true, exit = false))
            },
            userStrategySignalHistoryPort = InMemoryUserStrategySignalHistoryStore(),
            userStrategySignalPort = this
        )
        results = mutableMapOf()
    }

    @Test
    fun `BTC 상품코드로 실행`() {
        service.run(BTC_1MINUTE)

        val lastPrice = productPrice(2000, Duration.ofMinutes(1), now.plusMinutes(1))
        val expected = mapOf("id1" to OrderSignal(OrderType.BUY, lastPrice.time, lastPrice.price.close))

        assertEquals(expected, results, "OrderSignal 은 Candles#lastCandle 값으로 생성되야 한다")
    }

    @Test
    fun `ETH 상품 코드로 실행`() {
        service.run(ETH_1MINUTE)

        val lastPrice = productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0))
        val expected = mapOf("id3" to OrderSignal(OrderType.BUY, lastPrice.time, lastPrice.price.close))

        assertEquals(expected, results, "OrderSignal 은 Candles#lastCandle 값으로 생성되야 한다")
    }
}