package com.newy.algotrade.unit.rule

import com.newy.algotrade.domain.chart.Indicator
import com.newy.algotrade.domain.libs.ta4j.indicator.Taj4NumIndicatorWrapper
import com.newy.algotrade.domain.rule.CrossedDownRule
import com.newy.algotrade.domain.rule.CrossedUpRule
import com.newy.algotrade.domain.rule.OverRule
import com.newy.algotrade.domain.rule.UnderRule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.indicators.helpers.FixedDecimalIndicator
import kotlin.test.assertFalse


@DisplayName("지표값이 임계값(또는 다른 지표값) 위/아래에 위치하는 규칙 테스트")
class UpAndDownRuleTest {
    private val indicator = fixedDecimalIndicator(999, 1000, 1001)
    private val threshold = constDecimalIndicator(1000)

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
    private val threshold = constDecimalIndicator(1000)

    @Test
    fun `첫번째 index 는 항상 false 리턴`() {
        val indicator = fixedDecimalIndicator(2000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
    }

    @Test
    fun `임계값에 아래에 있다가 상승한 경우`() {
        val indicator = fixedDecimalIndicator(999, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "상승 돌파")
    }

    @Test
    fun `임계값에 아래에 있다가 임계값 까지만 상승한 경우`() {
        val indicator = fixedDecimalIndicator(999, 1000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "상승 돌파 아님")
    }

    @Test
    fun `임계값에 있다가 상승한 경우`() {
        val indicator = fixedDecimalIndicator(1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "상승 돌파 아님")
    }

    @Test
    fun `상승 후 입계값에 있다가 재상승한 경우`() {
        val indicator = fixedDecimalIndicator(999, 1500, 1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "상승 돌파 (999 -> 1500)")
        assertFalse(rule.isSatisfied(2))
        assertFalse(rule.isSatisfied(3), "상승 돌파 아님 (1000 -> 1500)")
    }

    @Test
    fun `상승 후 입계값 아래에 있다가 재상승한 경우`() {
        val indicator = fixedDecimalIndicator(999, 1500, 998, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "1번쨰 상승 돌파 (999 -> 1500)")
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "2번쨰 상승 돌파 (998 -> 1500)")
    }

    @Test
    fun `첫 번째 값과 마지막 값 사이에 입계값만 있는 경우`() {
        val indicator = fixedDecimalIndicator(999, 1000, 1500)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "상승 돌파 (999 -> 1000 -> 1500)")
    }
}

@DisplayName("하락 돌파 규칙 테스트")
class CrossedDownRuleTest {
    private val threshold = constDecimalIndicator(1000)

    @Test
    fun `첫번째 index 는 항상 false 리턴`() {
        val indicator = fixedDecimalIndicator(500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
    }

    @Test
    fun `임계값 위에 있다가 하락한 경우`() {
        val indicator = fixedDecimalIndicator(1001, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "하락 돌파")
    }

    @Test
    fun `임계값에 위에 있다가 임계값 까지만 하락한 경우`() {
        val indicator = fixedDecimalIndicator(1001, 1000)
        val rule = CrossedUpRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "하락 돌파 아님")
    }

    @Test
    fun `임계값에 있다가 하락한 경우`() {
        val indicator = fixedDecimalIndicator(1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1), "하락 돌파 아님")
    }

    @Test
    fun `하락 후 입계값에 있다가 재하락한 경우`() {
        val indicator = fixedDecimalIndicator(1001, 500, 1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "하락 돌파 (1001 -> 500)")
        assertFalse(rule.isSatisfied(2))
        assertFalse(rule.isSatisfied(3), "하락 돌파 아님 (1000 -> 500)")
    }

    @Test
    fun `하락 후 입계값 위에 있다가 재하락한 경우`() {
        val indicator = fixedDecimalIndicator(1001, 500, 1002, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertTrue(rule.isSatisfied(1), "1번쨰 하락 돌파 (1001 -> 500)")
        assertFalse(rule.isSatisfied(2))
        assertTrue(rule.isSatisfied(3), "2번째 하락 돌파 (1002 -> 500)")
    }

    @Test
    fun `첫 번째 값과 마지막 값 사이에 입계값만 있는 경우`() {
        val indicator = fixedDecimalIndicator(1001, 1000, 500)
        val rule = CrossedDownRule(indicator, threshold)

        assertFalse(rule.isSatisfied(0))
        assertFalse(rule.isSatisfied(1))
        assertTrue(rule.isSatisfied(2), "하락 돌파 (1001 -> 1000 -> 500)")
    }
}

private fun constDecimalIndicator(value: Number): Indicator =
    fixedDecimalIndicator(*(0..100).map { value }.toTypedArray())

private fun fixedDecimalIndicator(vararg values: Number): Indicator =
    Ta4jFixedDecimalIndicator(*values)

class Ta4jFixedDecimalIndicator(vararg values: Number) : Taj4NumIndicatorWrapper(
    FixedDecimalIndicator(BaseBarSeries(), *(values.map { it.toString() }.toTypedArray()))
)
