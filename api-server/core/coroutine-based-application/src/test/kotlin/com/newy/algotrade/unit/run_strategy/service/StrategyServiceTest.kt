package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.StrategyService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

private val userStrategyKey = UserStrategyKey(
    "user1",
    "BuyTripleRSIStrategy",
    productPriceKey("BTCUSDT", Duration.ofMinutes(1))
)

@DisplayName("port 호출 순서 확인")
class StrategyServiceTest : NoErrorCandleAdapter, StrategyCommandPort {
    private val methodCallLogs: MutableList<String> = mutableListOf()
    private val service = StrategyService(candlePort = this, strategyPort = this)

    override fun getCandles(key: ProductPriceKey): Candles {
        methodCallLogs.add("getCandles")
        return super.getCandles(key)
    }

    override fun addStrategy(key: UserStrategyKey, strategy: Strategy) {
        methodCallLogs.add("addStrategy")
    }

    override fun removeStrategy(key: UserStrategyKey) {
        methodCallLogs.add("removeStrategy")
    }

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    fun `setStrategy - port 호출 순서 확인`() {
        service.setStrategy(userStrategyKey)

        assertEquals(listOf("getCandles", "addStrategy"), methodCallLogs)
    }

    @Test
    fun `removeStrategy - port 호출 순서 확인`() {
        service.removeStrategy(userStrategyKey)

        assertEquals(listOf("removeStrategy"), methodCallLogs)
    }
}

interface NoErrorCandleAdapter : CandleQueryPort {
    override fun getCandles(key: ProductPriceKey): Candles {
        return DEFAULT_CHART_FACTORY.candles()
    }

    override fun hasCandles(key: ProductPriceKey): Boolean {
        TODO("Not yet implemented")
    }
}