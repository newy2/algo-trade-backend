package com.newy.algotrade.unit.chart.rule

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ClosePriceIndicator
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.rule.StopGainRule
import com.newy.algotrade.chart.domain.rule.StopLossRule
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StopGainRuleTest {
    @Test
    fun exitRule() {
        val candles = DEFAULT_CHART_FACTORY.candles().also {
            val now = OffsetDateTime.now()
            it.upsert(baseCandle(closePrice = 1000, startTime = now.plusMinutes(0)))
            it.upsert(baseCandle(closePrice = 1010, startTime = now.plusMinutes(1)))
            it.upsert(baseCandle(closePrice = 1100, startTime = now.plusMinutes(2)))
        }

        val history = StrategySignalHistory().also {
            it.add(
                StrategySignal(
                    OrderType.BUY,
                    candles.firstCandle.time,
                    candles.firstCandle.price.close
                )
            )
        }

        val closePriceIndicator = ClosePriceIndicator(candles)
        val rule = StopGainRule(closePriceIndicator, 5)

        assertFalse(rule.isSatisfied(0, history))
        assertFalse(rule.isSatisfied(1, history))
        assertTrue(rule.isSatisfied(2, history))
    }
}


@DisplayName("하락 추세일 때, 포지션 별 손절가/익절가 계산")
class DownFlowStopGainOrLossRuleTest {
    private lateinit var candles: Candles
    private lateinit var stopGainRule: Rule
    private lateinit var stopLossRule: Rule

    @BeforeEach
    fun setUp() {
        candles = DEFAULT_CHART_FACTORY.candles().also {
            val now = OffsetDateTime.now()
            it.upsert(baseCandle(closePrice = 1000, startTime = now.plusMinutes(0)))
            it.upsert(baseCandle(closePrice = 1000 * (1 - .09), startTime = now.plusMinutes(1)))
            it.upsert(baseCandle(closePrice = 1000 * (1 - .10), startTime = now.plusMinutes(2)))
            it.upsert(baseCandle(closePrice = 1000 * (1 - .11), startTime = now.plusMinutes(3)))
        }

        val closePriceIndicator = ClosePriceIndicator(candles)
        val percent = 10
        stopGainRule = StopGainRule(closePriceIndicator, percent)
        stopLossRule = StopLossRule(closePriceIndicator, percent)
    }

    @Test
    fun `하락 추세일 때, 숏 포지션인 경우 StopGainRule 이 통과됨`() {
        val shortPositionHistory = StrategySignalHistory().also {
            val firstCandle = candles.firstCandle
            it.add(StrategySignal(OrderType.SELL, firstCandle.time, firstCandle.price.close))
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, shortPositionHistory))
            assertFalse(isSatisfied(1, shortPositionHistory))
            assertTrue(isSatisfied(2, shortPositionHistory), "숏 포지션 10% 이상 이익 발생")
            assertTrue(isSatisfied(3, shortPositionHistory), "숏 포지션 10% 이상 이익 발생")
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, shortPositionHistory))
            assertFalse(isSatisfied(1, shortPositionHistory))
            assertFalse(isSatisfied(2, shortPositionHistory))
            assertFalse(isSatisfied(3, shortPositionHistory))
        }
    }

    @Test
    fun `하락 추세일 때, 롱 포지션(또는 현물)인 경우 StopLossRule 이 통과됨`() {
        val longPositionHistory = StrategySignalHistory().also {
            val firstCandle = candles.firstCandle
            it.add(StrategySignal(OrderType.BUY, firstCandle.time, firstCandle.price.close))
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, longPositionHistory))
            assertFalse(isSatisfied(1, longPositionHistory))
            assertFalse(isSatisfied(2, longPositionHistory))
            assertFalse(isSatisfied(3, longPositionHistory))
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, longPositionHistory))
            assertFalse(isSatisfied(1, longPositionHistory))
            assertTrue(isSatisfied(2, longPositionHistory), "롱 포지션 10% 이상 손해 발생")
            assertTrue(isSatisfied(3, longPositionHistory), "롱 포지션 10% 이상 손해 발생")
        }
    }
}

@DisplayName("상승 추세일 때, 포지션 별 손절가/익절가 계산")
class UpFlowStopGainOrLossRuleTest {
    private lateinit var candles: Candles
    private lateinit var stopGainRule: Rule
    private lateinit var stopLossRule: Rule

    @BeforeEach
    fun setUp() {
        candles = DEFAULT_CHART_FACTORY.candles().also {
            val now = OffsetDateTime.now()
            it.upsert(baseCandle(closePrice = 1000, startTime = now.plusMinutes(0)))
            it.upsert(baseCandle(closePrice = 1000 * (1 + .09), startTime = now.plusMinutes(1)))
            it.upsert(baseCandle(closePrice = 1000 * (1 + .10), startTime = now.plusMinutes(2)))
            it.upsert(baseCandle(closePrice = 1000 * (1 + .11), startTime = now.plusMinutes(3)))
        }

        val closePriceIndicator = ClosePriceIndicator(candles)
        val percent = 10
        stopGainRule = StopGainRule(closePriceIndicator, percent)
        stopLossRule = StopLossRule(closePriceIndicator, percent)
    }

    @Test
    fun `상승 추세일 때, 숏 포지션인 경우 StopLossRule 이 통과됨`() {
        val shortPositionHistory = StrategySignalHistory().also {
            val firstCandle = candles.firstCandle
            it.add(StrategySignal(OrderType.SELL, firstCandle.time, firstCandle.price.close))
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, shortPositionHistory))
            assertFalse(isSatisfied(1, shortPositionHistory))
            assertFalse(isSatisfied(2, shortPositionHistory))
            assertFalse(isSatisfied(3, shortPositionHistory))
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, shortPositionHistory))
            assertFalse(isSatisfied(1, shortPositionHistory))
            assertTrue(isSatisfied(2, shortPositionHistory), "숏 포지션 10% 이상 손해 발생")
            assertTrue(isSatisfied(3, shortPositionHistory), "숏 포지션 10% 이상 손해 발생")
        }
    }

    @Test
    fun `상승 추세일 때, 롱 포지션(또는 현물)인 경우 StopGainRule 이 통과됨`() {
        val longPositionHistory = StrategySignalHistory().also {
            val firstCandle = candles.firstCandle
            it.add(StrategySignal(OrderType.BUY, firstCandle.time, firstCandle.price.close))
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, longPositionHistory))
            assertFalse(isSatisfied(1, longPositionHistory))
            assertTrue(isSatisfied(2, longPositionHistory), "롱 포지션 10% 이상 이익 발생")
            assertTrue(isSatisfied(3, longPositionHistory), "롱 포지션 10% 이상 이익 발생")
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, longPositionHistory))
            assertFalse(isSatisfied(1, longPositionHistory))
            assertFalse(isSatisfied(2, longPositionHistory))
            assertFalse(isSatisfied(3, longPositionHistory))
        }
    }
}

private fun baseCandle(closePrice: Number, startTime: OffsetDateTime) =
    Candle.TimeFrame.M1(
        startTime,
        openPrice = 1000.toBigDecimal(),
        highPrice = 2000.toBigDecimal(),
        lowPrice = 500.toBigDecimal(),
        closePrice = closePrice.toDouble().toBigDecimal(),
        volume = 0.toBigDecimal(),
    )
