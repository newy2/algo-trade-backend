package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.GetCandlePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.SetStrategyService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
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
class SetStrategyServiceTest : NoErrorCandleAdapter, NoErrorStrategyAdapter {
    private val methodCallLogs: MutableList<String> = mutableListOf()

    override fun getCandles(key: ProductPriceKey): Candles {
        methodCallLogs.add("getCandles")
        return super.getCandles(key)
    }

    override fun addStrategy(key: UserStrategyKey, strategy: Strategy) {
        methodCallLogs.add("addStrategy")
    }

    @Test
    fun `port 호출 순서 확인`() {
        val service = SetStrategyService(this, this)

        service.setStrategy(userStrategyKey)

        assertEquals(listOf("getCandles", "addStrategy"), methodCallLogs)
    }
}

interface NoErrorStrategyAdapter : StrategyCommandPort {
    override fun removeStrategy(key: UserStrategyKey) {
        TODO("Not yet implemented")
    }
}

interface NoErrorCandleAdapter : GetCandlePort {
    override fun getCandles(key: ProductPriceKey): Candles {
        return DEFAULT_CHART_FACTORY.candles()
    }
}