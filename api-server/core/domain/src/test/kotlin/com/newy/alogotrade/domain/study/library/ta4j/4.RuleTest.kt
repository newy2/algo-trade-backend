package com.newy.alogotrade.domain.study.library.ta4j

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.indicators.helpers.FixedDecimalIndicator
import org.ta4j.core.indicators.helpers.FixedIndicator
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.rules.*
import org.ta4j.core.rules.helper.ChainLink

@DisplayName("지표값이 임계값(또는 다른 지표값) 위/아래에 위치하는 경우에 사용")
class UpAndDownRuleTest {
    @Test
    fun `OverIndicatorRule - 임계값 초과인 경우 통과`() {
        val indicator = fixedDecimalIndicator(900, 1000, 1100)
        val rule = OverIndicatorRule(indicator, 1000)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "x > 1000")
    }

    @Test
    fun `UnderIndicatorRule - 임계값 미만인 경우 통과`() {
        val indicator = fixedDecimalIndicator(900, 1000, 1100)
        val rule = UnderIndicatorRule(indicator, 1000)

        assertTrue(rule.isSatisfied(0), "x < 1000")
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
    }

    @Test
    fun `InPipeRule - 임계값 사이에 있는 경우 통과`() {
        val indicator = fixedDecimalIndicator(800, 900, 1000, 1100, 1200)
        val maximum = 1100
        val minimum = 900
        val rule = InPipeRule(indicator, maximum, minimum)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "900 <= x <= 1100")
        assertTrue(rule.isSatisfied(2), "900 <= x <= 1100")
        assertTrue(rule.isSatisfied(3), "900 <= x <= 1100")
        assertFalse(rule.isSatisfied(4))
    }
}

@DisplayName("지표가 교차하는 경우에 사용")
class CrossRuleTest {
    @Test
    fun `CrossedUpIndicatorRule - 교차 상승하는 경우 통과`() {
        val indicator = fixedDecimalIndicator(800, 900, 1000, 1100, 1200)
        val rule = CrossedUpIndicatorRule(indicator, 1000)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "상승 지점 (1000 -> 1100)")
        assertFalse(rule.isSatisfied(4))
    }

    @Test
    fun `CrossedDownIndicatorRule - 교차 하락하는 경우 통과`() {
        val indicator = fixedDecimalIndicator(1200, 1100, 1000, 900, 800)
        val rule = CrossedDownIndicatorRule(indicator, 1000)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "하락 지점 (1000 -> 900)")
        assertFalse(rule.isSatisfied(4))
    }

    class CrossRuleBugTest {
        @Test
        fun `주의 - Indicator 의 첫 번째값 과 마지막 값 사이에 임계값만 있는 경우 작동하지 않음`() {
            // TODO 라이브러리 신규 버전 배표시, 업데이트 할 예정 (작성 시점 라이브러리 버전: 0.15)
            // 참고: https://github.com/ta4j/ta4j/pull/1138

            val indicator = fixedDecimalIndicator(900, 1000, 1100)
            val rule = CrossedUpIndicatorRule(indicator, 1000)

            assertFalse(rule.isSatisfied(0))
            assertFalse(rule.isSatisfied(1))
            assertFalse(rule.isSatisfied(2), "버그; 상승 지점을 파악하지 못함 (1000 -> 1100)")
        }
    }
}

@DisplayName("최근 barCount 내에서 상승/하락 '횟수'에 관심있는 경우 사용")
class RisingFallingRateRuleTest {
    private val passingRate = 2.0 / 3.0 // 상승, 하락 비율이 3분의 2이상인 경우에만 통과
    private val barCountToCheck = 3

    @Test
    fun `IsRisingRule - 상승한 비율이 많은 경우 통과`() {
        val indicator = fixedDecimalIndicator(1000, 500, 1000, 1100)
        val rule = IsRisingRule(indicator, barCountToCheck, passingRate)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "상승 누적 금액이 아닌, 상승 횟수의 비율로 계산됨 [하락(1000->500), 상승(500->1000), 상승(1000->1100)]")
    }

    @Test
    fun `IsFallingRule - 하락한 비율이 많은 경우 통과`() {
        val indicator = fixedDecimalIndicator(1000, 1100, 1000, 500)
        val rule = IsFallingRule(indicator, barCountToCheck, passingRate)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "하락 누적 금액이 아닌, 하락 횟수의 비율로 계산됨[상승(1000->1100), 하락(1100->1000), 하락(1000->500)]")
    }
}

@DisplayName("그 외 유용한 규칙")
class EtcRule {
    @Test
    fun `JustOnceRule - 첫번째 호출만 규칙을 검증하고, 이후 호출부터 항상 false 리턴함`() {
        val rule = JustOnceRule(booleanRule(true))

        assertTrue(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(0), "첫 번째 검증 이후, 항상 false 리턴")
        assertFalse(rule.isSatisfied(0), "첫 번째 검증 이후, 항상 false 리턴")
    }

    @Test
    fun `InSlopeRule - '현재 index'와 '이전 index'의 차이값이 경계값 내에 있는 경우 통과`() {
        val indicator = fixedDecimalIndicator(1000, 1009, 1019, 1119, 1220)
        val beforeIndexCount = 1
        val minSlope = DecimalNum.valueOf(10)
        val maxSlope = DecimalNum.valueOf(100)
        val rule = InSlopeRule(indicator, beforeIndexCount, minSlope, maxSlope)

        assertFalse(rule.isSatisfied(0), "10 <= (0 = 1000 - 1000) <= 100")
        assertFalse(rule.isSatisfied(1), "10 <= (9 = 1009 - 1000) <= 100")
        assertTrue(rule.isSatisfied(2), "10 <= (10 = 1019 - 1009) <= 100")
        assertTrue(rule.isSatisfied(3), "10 <= (100 = 1300 - 1200) <= 100")
        assertFalse(rule.isSatisfied(4), "10 <= (101 = 1220 - 1119) <= 100")
    }

    @DisplayName("트리거 Rule 이 통과하고, barCount 내에서 다른 Rule 이 통과해야 하는 경우 사용 - ChainRule")
    class ChainRule {
        @Test
        fun `ChainLink 의 Rule 이 실패하는 경우`() {
            val triggerRule = booleanRule(false, true)
            val linkRule = booleanRule(true, false)

            val beforeBarCount = 0
            val chainLink = ChainLink(linkRule, beforeBarCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertFalse(rule.isSatisfied(0))
            assertFalse(rule.isSatisfied(1), "chainLink[1] != true")
        }

        @Test
        fun `ChainLink 의 Rule 이 성공하는 경우`() {
            val triggerRule = booleanRule(false, true)
            val linkRule = booleanRule(true, false)

            val beforeBarCount = 1
            val chainLink = ChainLink(linkRule, beforeBarCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertFalse(rule.isSatisfied(0))
            assertTrue(rule.isSatisfied(1), "(chainLink[1] || chainLink[0]) == true")
        }

        @Test
        fun `주의 - ChainLink 의 'threshold' 는 '이전 ChainLink 의 index 를 기준'으로 사용된다`() {
            // 참고: https://github.com/ta4j/ta4j/pull/472#review-thread-or-comment-id-218945009

            val triggerRule = booleanRule(true, true, true, true)
            val linkRule1 = booleanRule(false, true, false, true)
            val linkRule2 = booleanRule(true, false, false, false)

            val chainLink1 = ChainLink(linkRule1, 0)
            val chainLink2 = ChainLink(linkRule2, 1)
            val rule = ChainRule(triggerRule, chainLink1, chainLink2)

            assertFalse(rule.isSatisfied(0))
            assertTrue(rule.isSatisfied(1), "chainLink1[1] == true && (chainLink2[1] || chainLink2[0]) == true")
            assertFalse(rule.isSatisfied(2))
            assertFalse(rule.isSatisfied(3), "chainLink1[3] == true && (chainLink2[3] || chainLink2[2]) != true")
        }
    }
}

@DisplayName("조건식 규칙 사용법")
class ConditionRuleTest {
    private lateinit var expression1: Rule
    private lateinit var expression2: Rule

    @BeforeEach
    fun setUp() {
        expression1 = booleanRule(true, false, true, false)
        expression2 = booleanRule(false, true, true, false)
    }

    @Test
    fun `AND 조건식`() {
        arrayOf(
            AndRule(expression1, expression2),
            expression1.and(expression2)
        ).forEach {
            assertFalse(it.isSatisfied(0))
            assertFalse(it.isSatisfied(1))
            assertTrue(it.isSatisfied(2))
            assertFalse(it.isSatisfied(3))
        }
    }

    @Test
    fun `OR 조건식`() {
        arrayOf(
            OrRule(expression1, expression2),
            expression1.or(expression2)
        ).forEach {
            assertTrue(it.isSatisfied(0))
            assertTrue(it.isSatisfied(1))
            assertTrue(it.isSatisfied(2))
            assertFalse(it.isSatisfied(3))
        }
    }

    @Test
    fun `XOR 조건식`() {
        arrayOf(
            XorRule(expression1, expression2),
            expression1.xor(expression2)
        ).forEach {
            assertTrue(it.isSatisfied(0))
            assertTrue(it.isSatisfied(1))
            assertFalse(it.isSatisfied(2))
            assertFalse(it.isSatisfied(3))
        }
    }

    @Test
    fun `Not 조건식`() {
        val rule = NotRule(expression1)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3))
    }

    @Test
    fun `중첩 조건식`() {
        val rule = AndRule(
            AndRule(expression1, expression2),
            OrRule(expression1, expression2),
        )

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2))
        assertFalse(rule.isSatisfied(3))
    }
}

fun fixedDecimalIndicator(vararg values: Number) =
    FixedDecimalIndicator(BaseBarSeries(), *(values.map { it.toString() }.toTypedArray()))

fun booleanRule(vararg values: Boolean) =
    BooleanIndicatorRule(FixedIndicator(BaseBarSeries(), *(values.toTypedArray())))