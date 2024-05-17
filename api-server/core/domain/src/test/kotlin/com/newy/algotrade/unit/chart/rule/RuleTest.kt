package com.newy.algotrade.unit.chart.rule

import com.newy.algotrade.domain.chart.indicator.ConstDecimalIndicator
import com.newy.algotrade.domain.chart.rule.*
import helpers.BooleanArrayRule
import helpers.BooleanRule
import helpers.FixedDecimalIndicator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse


@DisplayName("지표값이 임계값(또는 다른 지표값) 위/아래에 위치하는 규칙 테스트")
class UpAndDownRuleTest {
    private val indicator = FixedDecimalIndicator(999, 1000, 1001)
    private val threshold = ConstDecimalIndicator(1000)

    @Test
    fun `임계값 초과 규칙`() {
        val rule = OverRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "x > 1000")
    }

    @Test
    fun `임계값 미만 규칙`() {
        val rule = UnderRule(indicator, threshold)

        assertTrue(rule.isSatisfied(0), "x < 1000")
        assertFalse(rule.isSatisfied(1))
        assertFalse(rule.isSatisfied(2))
    }
}

@DisplayName("상승 돌파 규칙 테스트")
class CrossedUpRuleTest {
    private val threshold = 1000

    @Test
    fun `첫번째 index 는 항상 false 리턴`() {
        val indicator = FixedDecimalIndicator(2000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
    }

    @Test
    fun `임계값에 아래에 있다가 상승한 경우`() {
        val indicator = FixedDecimalIndicator(999, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "상승 돌파")
    }

    @Test
    fun `임계값에 아래에 있다가 임계값 까지만 상승한 경우`() {
        val indicator = FixedDecimalIndicator(999, 1000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "상승 돌파 아님")
    }

    @Test
    fun `임계값에 있다가 상승한 경우`() {
        val indicator = FixedDecimalIndicator(1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "상승 돌파 아님")
    }

    @Test
    fun `상승 후 입계값에 있다가 재상승한 경우`() {
        val indicator = FixedDecimalIndicator(999, 1500, 1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "상승 돌파 (999 -> 1500)")
        assertFalse(rule.isSatisfied(2))
        assertFalse(rule.isSatisfied(3), "상승 돌파 아님 (1000 -> 1500)")
    }

    @Test
    fun `상승 후 입계값 아래에 있다가 재상승한 경우`() {
        val indicator = FixedDecimalIndicator(999, 1500, 998, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "1번쨰 상승 돌파 (999 -> 1500)")
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "2번쨰 상승 돌파 (998 -> 1500)")
    }

    @Test
    fun `첫 번째 값과 마지막 값 사이에 입계값만 있는 경우`() {
        val indicator = FixedDecimalIndicator(999, 1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "상승 돌파 (999 -> 1000 -> 1500)")
    }
}

@DisplayName("하락 돌파 규칙 테스트")
class CrossedDownRuleTest {
    private val threshold = 1000

    @Test
    fun `첫번째 index 는 항상 false 리턴`() {
        val indicator = FixedDecimalIndicator(500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
    }

    @Test
    fun `임계값 위에 있다가 하락한 경우`() {
        val indicator = FixedDecimalIndicator(1001, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "하락 돌파")
    }

    @Test
    fun `임계값에 위에 있다가 임계값 까지만 하락한 경우`() {
        val indicator = FixedDecimalIndicator(1001, 1000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "하락 돌파 아님")
    }

    @Test
    fun `임계값에 있다가 하락한 경우`() {
        val indicator = FixedDecimalIndicator(1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "하락 돌파 아님")
    }

    @Test
    fun `하락 후 입계값에 있다가 재하락한 경우`() {
        val indicator = FixedDecimalIndicator(1001, 500, 1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "하락 돌파 (1001 -> 500)")
        assertFalse(rule.isSatisfied(2))
        assertFalse(rule.isSatisfied(3), "하락 돌파 아님 (1000 -> 500)")
    }

    @Test
    fun `하락 후 입계값 위에 있다가 재하락한 경우`() {
        val indicator = FixedDecimalIndicator(1001, 500, 1002, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "1번쨰 하락 돌파 (1001 -> 500)")
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "2번째 하락 돌파 (1002 -> 500)")
    }

    @Test
    fun `첫 번째 값과 마지막 값 사이에 입계값만 있는 경우`() {
        val indicator = FixedDecimalIndicator(1001, 1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "하락 돌파 (1001 -> 1000 -> 500)")
    }
}

@DisplayName("그 외 유용한 규칙")
class EtcRule {
    @DisplayName("ChainRule - 순차적으로 규칙이 실행되야 하는 경우에 사용. (트리거 Rule 이 성공하면, ChainLink 의 Rule 을 순차적으로 검증한다)")
    class ChainRuleTest {
        @Test
        fun `ChainLink 의 beforeIndexCount(threshold) 가 0 인 경우, linkRule 이 사용 가능한 index 는 triggerRule 가 성공한 index 이다`() {
            val triggerRule = BooleanArrayRule(true, true, true)
            val linkRule = BooleanArrayRule(true, false, false)

            val beforeIndexCount = 0
            val chainLink = ChainLink(linkRule, beforeIndexCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertTrue(rule.isSatisfied(0), "linkRule[0] == true")
            assertFalse(rule.isSatisfied(1), "linkRule[1] != true")
            assertFalse(rule.isSatisfied(2), "linkRule[2] != true")
        }

        @Test
        fun `ChainLink 의 beforeIndexCount(threshold) 가 0 이상인 경우, linkRule 이 사용 가능한 index 는 triggerRule 이 성공한 index 에서 (index - beforeIndexCount) 까지 이다`() {
            val triggerRule = BooleanArrayRule(true, true, true)
            val linkRule = BooleanArrayRule(true, false, false)

            val beforeIndexCount = 1
            val chainLink = ChainLink(linkRule, beforeIndexCount)
            val rule = ChainRule(triggerRule, chainLink)

            assertTrue(rule.isSatisfied(0), "linkRule[0] == true")
            assertTrue(rule.isSatisfied(1), "(linkRule[1] || linkRule[0]) == true")
            Assertions.assertFalse(rule.isSatisfied(2), "(linkRule[2] || linkRule[1]) != true")
        }

        @Test
        fun `주의 - ChainLink 를 여러 개 사용하는 경우, ChainLink 의 beforeIndexCount 는 직전에 성공한 ChainLink 의 index 기준으로 계산한다`() {
            val triggerRule = BooleanArrayRule(true, true, true, true, true)
            val linkRule1 = BooleanArrayRule(false, false, true, false, false)
            val linkRule2 = BooleanArrayRule(true, false, false, false, false)

            val chainLink1 = ChainLink(linkRule1, 1)
            val chainLink2 = ChainLink(linkRule2, 2)
            val rule = ChainRule(triggerRule, chainLink1, chainLink2)

            Assertions.assertFalse(rule.isSatisfied(0))
            Assertions.assertFalse(rule.isSatisfied(1))
            assertTrue(
                rule.isSatisfied(2),
                "linkRule1[2] == true && (linkRule2[2] || linkRule2[1] || linkRule2[0]) == true"
            )
            assertTrue(
                rule.isSatisfied(3),
                "(linkRule1[3] || linkRule1[2]) == true && (linkRule2[2] || linkRule2[1] || linkRule2[0]) == true"
            )
            Assertions.assertFalse(rule.isSatisfied(4))
        }
    }
}

@DisplayName("조건 룰 테스트")
class ConditionRuleTest {
    @Test
    fun `AND 조건 룰`() {
        assertTrue(AndRule(BooleanRule(true), BooleanRule(true)).isSatisfied(0))
        assertFalse(AndRule(BooleanRule(true), BooleanRule(false)).isSatisfied(0))
        assertFalse(AndRule(BooleanRule(false), BooleanRule(true)).isSatisfied(0))
        assertFalse(AndRule(BooleanRule(false), BooleanRule(false)).isSatisfied(0))
    }

    @Test
    fun `OR 조건 룰`() {
        assertTrue(OrRule(BooleanRule(true), BooleanRule(true)).isSatisfied(0))
        assertTrue(OrRule(BooleanRule(true), BooleanRule(false)).isSatisfied(0))
        assertTrue(OrRule(BooleanRule(false), BooleanRule(true)).isSatisfied(0))
        assertFalse(OrRule(BooleanRule(false), BooleanRule(false)).isSatisfied(0))
    }
}