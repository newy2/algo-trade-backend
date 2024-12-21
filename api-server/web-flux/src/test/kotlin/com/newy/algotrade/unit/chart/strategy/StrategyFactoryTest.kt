package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.chart.domain.strategy.Strategy
import com.newy.algotrade.chart.domain.strategy.custom.BuyTripleRSIStrategy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@DisplayName("팩토리 메소드 테스트")
class StrategyFactoryTest {
    @Test
    fun `클레스 이름으로 Strategy 객체 생성하기`() {
        val candles = DEFAULT_CHART_FACTORY.candles()
        val tripleRSIStrategy = Strategy.fromClassName("BuyTripleRSIStrategy", candles)

        assertTrue(tripleRSIStrategy is BuyTripleRSIStrategy)
    }
}