package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.*
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyRunner
import helpers.BooleanRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime

private fun oneMinuteCandle(beginTime: OffsetDateTime, closePrice: Int) =
    Candle.TimeFrame.M1(
        beginTime,
        openPrice = 100.toDouble().toBigDecimal(),
        lowPrice = 100.toDouble().toBigDecimal(),
        highPrice = (closePrice * 2).toDouble().toBigDecimal(),
        closePrice = closePrice.toDouble().toBigDecimal(),
        volume = BigDecimal.ZERO,
    )

@DisplayName("진입 신호만 발생하는 StrategyRunner 테스트")
class EntryRuleStrategyRunnerTest {
    private val now = OffsetDateTime.now()
    private lateinit var candles: Candles
    private lateinit var history: OrderSignalHistory
    private lateinit var runner: StrategyRunner

    @BeforeEach
    fun setUp() {
        candles = ChartFactory.TA4J.createCandles()
        history = OrderSignalHistory()
        runner = StrategyRunner(
            candles,
            strategy = Strategy(
                entryOrderType = OrderType.BUY,
                entryRule = BooleanRule(true),
                exitRule = BooleanRule(false),
            ),
            history
        )
    }

    @Test
    fun `가격 정보가 1개만 업데이트 된 경우`() {
        val result = runner.run(listOf(oneMinuteCandle(now, 1000)))

        assertEquals(result, OrderSignal(OrderType.BUY, candles.lastCandle.time))
    }

    @Test
    fun `가격 정보가 2개가 업데이트 된 경우`() {
        val result = runner.run(
            listOf(
                oneMinuteCandle(now, 1000),
                oneMinuteCandle(now.plusMinutes(1), 2000),
            )
        )

        assertEquals(result, OrderSignal(OrderType.BUY, candles.lastCandle.time))
    }

    @Test
    fun `StrategyRunner_run 호출 시 candles, orderSignalHistory 가 업데이트 됨`() {
        runner.run(
            listOf(
                oneMinuteCandle(now.plusMinutes(0), 1000),
                oneMinuteCandle(now.plusMinutes(1), 2000),
            )
        )

        assertEquals(2, candles.size, "Candles 업데이트 됨")
        assertEquals(oneMinuteCandle(now.plusMinutes(0), 1000), candles.firstCandle)
        assertEquals(oneMinuteCandle(now.plusMinutes(1), 2000), candles.lastCandle)

        assertEquals(OrderType.BUY, history.lastOrderType(), "OrderSignalHistory 업데이트 됨")
    }
}

@DisplayName("진입 신호가 아닌 경우에 대한 StrategyRunner 테스트")
class OtherSignalStrategyRunnerTest {
    private val now = OffsetDateTime.now()
    private lateinit var candles: Candles
    private lateinit var history: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        candles = ChartFactory.TA4J.createCandles()
        history = OrderSignalHistory().also {
            it.add(OrderSignal(OrderType.BUY, Candle.TimeRange(Duration.ofMinutes(1), now)))
        }
    }

    @Test
    fun `진출 신호가 발생하는 경우`() {
        val candles = ChartFactory.TA4J.createCandles()
        val runner = StrategyRunner(
            candles,
            strategy = Strategy(
                entryOrderType = OrderType.BUY,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(true),
            ),
            history
        )

        val result = runner.run(listOf(oneMinuteCandle(now, 1000)))

        assertEquals(result, OrderSignal(OrderType.SELL, candles.lastCandle.time))
        assertEquals(OrderType.SELL, history.lastOrderType(), "OrderSignalHistory 업데이트 됨")
    }

    @Test
    fun `아무 신호도 발생하지 않는 경우`() {
        val runner = StrategyRunner(
            candles,
            strategy = Strategy(
                entryOrderType = OrderType.BUY,
                entryRule = BooleanRule(false),
                exitRule = BooleanRule(false),
            ),
            history
        )

        val result = runner.run(listOf(oneMinuteCandle(now, 1000)))

        assertEquals(result, OrderSignal(OrderType.NONE, candles.lastCandle.time))
        assertEquals(OrderType.BUY, history.lastOrderType(), "OrderType.NONE 은 추가하지 않음")
    }
}