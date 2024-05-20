package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@DisplayName("팩토리 메소드 테스트")
class StrategyFactoryTest {
    @Test
    fun test() {
        val candles = DEFAULT_CHART_FACTORY.candles()
        val tripleRSIStrategy = Strategy.create(StrategyId.from("1"), candles)

        assertTrue(tripleRSIStrategy is BuyTripleRSIStrategy)
        assertEquals("0.0.1", tripleRSIStrategy.version())
    }
}