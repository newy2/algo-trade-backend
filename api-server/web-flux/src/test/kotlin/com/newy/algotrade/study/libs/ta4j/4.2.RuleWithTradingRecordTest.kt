package com.newy.algotrade.study.libs.ta4j

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ta4j.core.*
import org.ta4j.core.Trade.TradeType
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.rules.OpenedPositionMinimumBarCountRule
import org.ta4j.core.rules.StopGainRule
import org.ta4j.core.rules.StopLossRule
import org.ta4j.core.rules.WaitForRule
import java.time.ZonedDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@DisplayName("포지션 오픈 이후 barCount 만큼 기다리는 경우 사용")
class OpenedPositionMinimumBarCountRuleTest {
    private lateinit var record: TradingRecord
    private lateinit var rule: Rule

    @BeforeEach
    fun setUp() {
        record = BaseTradingRecord()
        rule = OpenedPositionMinimumBarCountRule(2)
    }

    @Test
    fun `거래가 없는 경우 항상 false 리턴`() {
        (0..10).forEach { index ->
            assertFalse(rule.isSatisfied(index, record))
        }
    }

    @Test
    fun `거래 진입 index + N 부터 true 리턴`() {
        record.enter(1)

        assertFalse(rule.isSatisfied(0, record))
        assertFalse(rule.isSatisfied(1, record), "거래 진입 index")
        assertFalse(rule.isSatisfied(2, record))
        assertTrue(rule.isSatisfied(3, record), "거래 진입 index + 2")
        assertTrue(rule.isSatisfied(4, record))
    }

    @Test
    fun `waitCount 가 0 이면 에러`() {
        assertThrows<IllegalArgumentException> {
            val zeroWaitCount = 0
            OpenedPositionMinimumBarCountRule(zeroWaitCount)
        }
    }
}

@DisplayName("WaitForRule 테스트")
class WaitForRuleTest {
    private lateinit var record: TradingRecord
    private lateinit var rule: Rule

    @BeforeEach
    fun setUp() {
        record = BaseTradingRecord(TradeType.BUY)
        rule = WaitForRule(TradeType.BUY, 2)
    }

    @Test
    fun `거래가 없는 경우 항상 false 리턴`() {
        (0..10).forEach { index ->
            assertFalse(rule.isSatisfied(index, record))
        }
    }

    @Test
    fun `거래 진입 index + N 부터 true 리턴`() {
        record.enter(1)

        assertFalse(rule.isSatisfied(0, record))
        assertFalse(rule.isSatisfied(1, record), "거래 진입 index")
        assertFalse(rule.isSatisfied(2, record))
        assertTrue(rule.isSatisfied(3, record), "거래 진입 index + 2")
        assertTrue(rule.isSatisfied(4, record))
    }

    @DisplayName("WaitForRule 특이사항 테스트")
    class WaitForRuleExceptionTest {
        @Test
        fun `waitBarCount 가 0 인 경우, 거래가 발생한 index 부터 true 리턴`() {
            val rule = WaitForRule(TradeType.BUY, 0)
            val record = BaseTradingRecord(TradeType.BUY).also {
                it.enter(1)
            }

            assertFalse(rule.isSatisfied(0, record))
            assertTrue(rule.isSatisfied(1, record), "거래 진입 index + 0")
            assertTrue(rule.isSatisfied(2, record))
        }

        @Test
        fun `TradeType 이 다른 경우 false 리턴`() {
            val rule = WaitForRule(TradeType.BUY, 0)
            val record = BaseTradingRecord(TradeType.SELL).also {
                it.enter(1)
            }

            assertFalse(rule.isSatisfied(0, record))
            assertFalse(rule.isSatisfied(1, record))
            assertFalse(rule.isSatisfied(2, record))
        }

        @Test
        fun `TradeType 이 다른 경우 false 리턴2`() {
            val rule = WaitForRule(TradeType.SELL, 0)
            val record = BaseTradingRecord(TradeType.BUY).also {
                it.enter(1)
            }

            assertFalse(rule.isSatisfied(0, record))
            assertFalse(rule.isSatisfied(1, record))
            assertFalse(rule.isSatisfied(2, record))
        }
    }
}

@DisplayName("하락 추세일 때, 포지션 별 손절가/익절가 계산")
class DownFlowStopGainOrLossRuleTest {
    private lateinit var series: BaseBarSeries
    private lateinit var stopGainRule: Rule
    private lateinit var stopLossRule: Rule

    @BeforeEach
    fun setUp() {
        series = BaseBarSeries().also {
            val now = ZonedDateTime.now()
            it.addBar(baseBar(closePrice = 1000, endTime = now.plusMinutes(0)))
            it.addBar(baseBar(closePrice = 1000 * (1 - .09), endTime = now.plusMinutes(1)))
            it.addBar(baseBar(closePrice = 1000 * (1 - .10), endTime = now.plusMinutes(2)))
            it.addBar(baseBar(closePrice = 1000 * (1 - .11), endTime = now.plusMinutes(3)))
        }

        val closePriceIndicator = ClosePriceIndicator(series)
        val percent = 10
        stopGainRule = StopGainRule(closePriceIndicator, percent)
        stopLossRule = StopLossRule(closePriceIndicator, percent)
    }

    @Test
    fun `하락 추세일 때, 숏 포지션인 경우 StopGainRule 이 통과됨`() {
        val shortPositionRecord = BaseTradingRecord(TradeType.SELL).also {
            it.enter(index = 0, price = 1000, amount = 1)
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, shortPositionRecord))
            assertFalse(isSatisfied(1, shortPositionRecord))
            assertTrue(isSatisfied(2, shortPositionRecord), "숏 포지션 10% 이상 이익 발생")
            assertTrue(isSatisfied(3, shortPositionRecord), "숏 포지션 10% 이상 이익 발생")
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, shortPositionRecord))
            assertFalse(isSatisfied(1, shortPositionRecord))
            assertFalse(isSatisfied(2, shortPositionRecord))
            assertFalse(isSatisfied(3, shortPositionRecord))
        }
    }

    @Test
    fun `하락 추세일 때, 롱 포지션(또는 현물)인 경우 StopLossRule 이 통과됨`() {
        val longPositionRecord = BaseTradingRecord(TradeType.BUY).also {
            it.enter(index = 0, price = 1000, amount = 1)
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, longPositionRecord))
            assertFalse(isSatisfied(1, longPositionRecord))
            assertFalse(isSatisfied(2, longPositionRecord))
            assertFalse(isSatisfied(3, longPositionRecord))
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, longPositionRecord))
            assertFalse(isSatisfied(1, longPositionRecord))
            assertTrue(isSatisfied(2, longPositionRecord), "롱 포지션 10% 이상 손해 발생")
            assertTrue(isSatisfied(3, longPositionRecord), "롱 포지션 10% 이상 손해 발생")
        }
    }
}

@DisplayName("상승 추세일 때, 포지션 별 손절가/익절가 계산")
class UpFlowStopGainOrLossRuleTest {
    private lateinit var series: BaseBarSeries
    private lateinit var stopGainRule: Rule
    private lateinit var stopLossRule: Rule

    @BeforeEach
    fun setUp() {
        series = BaseBarSeries().also {
            val now = ZonedDateTime.now()
            it.addBar(baseBar(closePrice = 1000, endTime = now.plusMinutes(0)))
            it.addBar(baseBar(closePrice = 1000 * (1 + .09), endTime = now.plusMinutes(1)))
            it.addBar(baseBar(closePrice = 1000 * (1 + .10), endTime = now.plusMinutes(2)))
            it.addBar(baseBar(closePrice = 1000 * (1 + .11), endTime = now.plusMinutes(3)))
        }

        val closePriceIndicator = ClosePriceIndicator(series)
        val percent = 10
        stopGainRule = StopGainRule(closePriceIndicator, percent)
        stopLossRule = StopLossRule(closePriceIndicator, percent)
    }

    @Test
    fun `상승 추세일 때, 숏 포지션인 경우 StopLossRule 이 통과됨`() {
        val shortPositionRecord = BaseTradingRecord(TradeType.SELL).also {
            it.enter(index = 0, price = 1000, amount = 1)
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, shortPositionRecord))
            assertFalse(isSatisfied(1, shortPositionRecord))
            assertFalse(isSatisfied(2, shortPositionRecord))
            assertFalse(isSatisfied(3, shortPositionRecord))
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, shortPositionRecord))
            assertFalse(isSatisfied(1, shortPositionRecord))
            assertTrue(isSatisfied(2, shortPositionRecord), "숏 포지션 10% 이상 손해 발생")
            assertTrue(isSatisfied(3, shortPositionRecord), "숏 포지션 10% 이상 손해 발생")
        }
    }

    @Test
    fun `상승 추세일 때, 롱 포지션(또는 현물)인 경우 StopGainRule 이 통과됨`() {
        val longPositionRecord = BaseTradingRecord(TradeType.BUY).also {
            it.enter(index = 0, price = 1000, amount = 1)
        }

        stopGainRule.run {
            assertFalse(isSatisfied(0, longPositionRecord))
            assertFalse(isSatisfied(1, longPositionRecord))
            assertTrue(isSatisfied(2, longPositionRecord), "롱 포지션 10% 이상 이익 발생")
            assertTrue(isSatisfied(3, longPositionRecord), "롱 포지션 10% 이상 이익 발생")
        }

        stopLossRule.run {
            assertFalse(isSatisfied(0, longPositionRecord))
            assertFalse(isSatisfied(1, longPositionRecord))
            assertFalse(isSatisfied(2, longPositionRecord))
            assertFalse(isSatisfied(3, longPositionRecord))
        }
    }
}

class StopGainRuleTest {
    @Test
    fun test() {
        val series = BaseBarSeries().also {
            val now = ZonedDateTime.now()
            it.addBar(baseBar(closePrice = 100, endTime = now.plusMinutes(0)))
            it.addBar(baseBar(closePrice = 101, endTime = now.plusMinutes(1)))
            it.addBar(baseBar(closePrice = 110, endTime = now.plusMinutes(2)))
        }

        val longPositionRecord = BaseTradingRecord(TradeType.BUY).also {
            it.enter(index = 0, price = 100, amount = 1)
        }
        val closePriceIndicator = ClosePriceIndicator(series)
        val rule = StopGainRule(closePriceIndicator, 5)
        assertFalse(rule.isSatisfied(0, longPositionRecord))
        assertFalse(rule.isSatisfied(1, longPositionRecord))
        assertTrue(rule.isSatisfied(2, longPositionRecord))
    }
}

fun TradingRecord.enter(index: Number, price: Number, amount: Number) =
    this.enter(index.toInt(), DecimalNum.valueOf(price), DecimalNum.valueOf(amount))

fun baseBar(closePrice: Number, endTime: ZonedDateTime) =
    BaseBar(
        1.minutes.toJavaDuration(),
        endTime,
        1000.toBigDecimal(),
        1000.toBigDecimal(),
        1000.toBigDecimal(),
        closePrice.toDouble().toBigDecimal(),
        0.toBigDecimal(),
    )
