package com.newy.algotrade.study.lib.ta4j

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
        val threshold = 1000
        val rule = OverIndicatorRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "x > 1000")
    }

    @Test
    fun `UnderIndicatorRule - 임계값 미만인 경우 통과`() {
        val indicator = fixedDecimalIndicator(900, 1000, 1100)
        val threshold = 1000
        val rule = UnderIndicatorRule(indicator, threshold)

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
    private val threshold = 1000

    @Test
    fun `CrossedUpIndicatorRule - 교차 상승하는 경우 통과`() {
        val indicator = fixedDecimalIndicator(800, 900, 1000, 1100, 1200)
        val rule = CrossedUpIndicatorRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "상승 지점 (1000 -> 1100)")
        assertFalse(rule.isSatisfied(4))
    }

    @Test
    fun `CrossedDownIndicatorRule - 교차 하락하는 경우 통과`() {
        val indicator = fixedDecimalIndicator(1200, 1100, 1000, 900, 800)
        val rule = CrossedDownIndicatorRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "하락 지점 (1000 -> 900)")
        assertFalse(rule.isSatisfied(4))
    }

    @Test
    fun `버그 - Indicator 의 첫 번째값 과 마지막 값 사이에 임계값만 있는 경우 작동하지 않음`() {
        // TODO 라이브러리 신규 버전 배표시, 업데이트 할 예정 (작성 시점 라이브러리 버전: 0.15)
        // 참고: https://github.com/ta4j/ta4j/pull/1138

        val indicator = fixedDecimalIndicator(900, 1000, 1100)
        val rule = CrossedUpIndicatorRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2), "버그; 상승 지점을 파악하지 못함 (1000 -> 1100)")
    }
}

@DisplayName("최근 barCount 내에서 상승/하락 '횟수'에 관심있는 경우 사용")
class RisingFallingRateRuleTest {
    private val passingRate = 2.0 / 3.0 // 상승, 하락 비율이 3분의 2이상인 경우에만 통과
    private val barCountToCheck = 3

    @Test
    fun `IsRisingRule - 상승한 횟수의 비율이 많은 경우 통과`() {
        val indicator = fixedDecimalIndicator(2000, 1000, 1100, 1200)
        val rule = IsRisingRule(indicator, barCountToCheck, passingRate)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(
            rule.isSatisfied(3),
            "상승 금액이 아닌, 상승 횟수의 비율로 계산됨 (총 상승횟수: 2회 > 총 하락횟수 1회 | 총 상승금액: 200 < 총 하락금액: 1000)"
        )
    }

    @Test
    fun `IsFallingRule - 하락한 횟수의 비율이 많은 경우 통과`() {
        val indicator = fixedDecimalIndicator(1000, 2000, 1900, 1800)
        val rule = IsFallingRule(indicator, barCountToCheck, passingRate)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
        assertTrue(
            rule.isSatisfied(3),
            "하락 금액이 아닌, 하락 횟수의 비율로 계산됨 (총 상승횟수: 1회 < 총 하락횟수 2회 | 총 상승금액: 1000 > 총 하락금액: 200)"
        )
    }
}

@DisplayName("그 외 유용한 규칙")
class EtcRule {
    @Test
    fun `JustOnceRule - 같은 index 로 여러번 검증 시, 첫번째 호출만 통과하고, 이후 호출부터 항상 false 리턴함`() {
        val rule = JustOnceRule(booleanRule(true))

        assertTrue(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(0), "첫 번째 검증 이후, 항상 false 리턴")
        assertFalse(rule.isSatisfied(0), "첫 번째 검증 이후, 항상 false 리턴")
    }

    @DisplayName("ChainRule - 순차적으로 규칙이 실행되야 하는 경우에 사용. (트리거 Rule 이 성공하면, ChainLink 의 Rule 을 순차적으로 검증한다)")
    class ChainRule {
        @Test
        fun `ChainLink 의 beforeIndexCount(threshold) 가 0 인 경우, linkRule 이 사용 가능한 index 는 triggerRule 가 성공한 index 이다`() {
            val triggerRule = booleanRule(true, true, true)
            val linkRule = booleanRule(true, false, false)

            val beforeIndexCount = 0
            val chainLink = ChainLink(linkRule, beforeIndexCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertTrue(rule.isSatisfied(0), "linkRule[0] == true")
            assertFalse(rule.isSatisfied(1), "linkRule[1] != true")
            assertFalse(rule.isSatisfied(2), "linkRule[2] != true")
        }

        @Test
        fun `ChainLink 의 beforeIndexCount(threshold) 가 0 이상인 경우, linkRule 이 사용 가능한 index 는 triggerRule 이 성공한 index 에서 (index - beforeIndexCount) 까지 이다`() {
            val triggerRule = booleanRule(true, true, true)
            val linkRule = booleanRule(true, false, false)

            val beforeIndexCount = 1
            val chainLink = ChainLink(linkRule, beforeIndexCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertTrue(rule.isSatisfied(0), "linkRule[0] == true")
            assertTrue(rule.isSatisfied(1), "(linkRule[1] || linkRule[0]) == true")
            assertFalse(rule.isSatisfied(2), "(linkRule[2] || linkRule[1]) != true")
        }

        @Test
        fun `주의 - ChainLink 를 여러 개 사용하는 경우, ChainLink 의 beforeIndexCount 는 직전에 성공한 ChainLink 의 index 기준으로 계산한다`() {
            // 참고: https://github.com/ta4j/ta4j/pull/472#review-thread-or-comment-id-218945009

            val triggerRule = booleanRule(true, true, true, true, true)
            val linkRule1 = booleanRule(false, false, true, false, false)
            val linkRule2 = booleanRule(true, false, false, false, false)

            val chainLink1 = ChainLink(linkRule1, 1)
            val chainLink2 = ChainLink(linkRule2, 2)
            val rule = ChainRule(triggerRule, chainLink1, chainLink2)

            assertFalse(rule.isSatisfied(0))
            assertFalse(rule.isSatisfied(1))
            assertTrue(
                rule.isSatisfied(2),
                "linkRule1[2] == true && (linkRule2[2] || linkRule2[1] || linkRule2[0]) == true"
            )
            assertTrue(
                rule.isSatisfied(3),
                "(linkRule1[3] || linkRule1[2]) == true && (linkRule2[2] || linkRule2[1] || linkRule2[0]) == true"
            )
            assertFalse(rule.isSatisfied(4))
        }
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
        arrayOf(
            NotRule(expression1),
            expression1.negation()
        ).forEach {
            assertFalse(it.isSatisfied(0))
            assertTrue(it.isSatisfied(1))
            assertFalse(it.isSatisfied(2))
            assertTrue(it.isSatisfied(3))
        }
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

private fun fixedDecimalIndicator(vararg values: Number) =
    FixedDecimalIndicator(BaseBarSeries(), *(values.map { it.toString() }.toTypedArray()))

private fun booleanRule(vararg values: Boolean) =
    BooleanIndicatorRule(FixedIndicator(BaseBarSeries(), *(values.toTypedArray())))