package com.newy.algotrade.domain.study.library.ta4j

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ta4j.core.BaseStrategy
import org.ta4j.core.Strategy
import kotlin.test.assertTrue


class BaseStrategyTest {
    private lateinit var strategy: Strategy

    @BeforeEach
    fun setUp() {
        val entryRule = booleanRule(true, false, false)
        val exitRule = booleanRule(false, false, true)
        strategy = BaseStrategy(entryRule, exitRule)
    }

    @Test
    fun `포지션 오픈 가능 여부 확인`() {
        assertTrue(strategy.shouldEnter(0))
        assertFalse(strategy.shouldEnter(1))
        assertFalse(strategy.shouldEnter(2))
    }

    @Test
    fun `포지션 종료 가능 여부 확인`() {
        assertFalse(strategy.shouldExit(0))
        assertFalse(strategy.shouldExit(1))
        assertTrue(strategy.shouldExit(2))
    }
}

class BaseStrategyWithUnstablePeriodTest {
    private lateinit var strategy: Strategy

    @BeforeEach
    fun setUp() {
        /***
         * 참고: https://ta4j.github.io/ta4j-wiki/FAQ.html#why-does-my-indicator-not-match-someone-elses-values
         *
         * (특정 Indicator 사용 시) 과거 index 의 데이터가 적은 경우 오차가 발생하는 경우가 있다.
         * 백테스트 로직에서 BaseStrategy 에 skipIndex 를 사용하면 해당 이슈를 제거할 수 있다.
         */
        val entryRule = booleanRule(true, true, true)
        val exitRule = booleanRule(true, true, true)
        val skipIndex = 2
        strategy = BaseStrategy(entryRule, exitRule, skipIndex)
    }

    @Test
    fun `포지션 오픈 가능 여부 확인`() {
        assertFalse(strategy.shouldEnter(0), "skip 됨")
        assertFalse(strategy.shouldEnter(1), "skip 됨")
        assertTrue(strategy.shouldEnter(2))
    }

    @Test
    fun `포지션 종료 가능 여부 확인`() {
        assertFalse(strategy.shouldExit(0), "skip 됨")
        assertFalse(strategy.shouldExit(1), "skip 됨")
        assertTrue(strategy.shouldExit(2))
    }
}