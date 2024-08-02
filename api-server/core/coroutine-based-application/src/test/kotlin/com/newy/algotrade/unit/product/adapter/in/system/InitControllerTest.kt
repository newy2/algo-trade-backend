package com.newy.algotrade.unit.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system.InitController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

// TODO Remove this
class InitControllerTest : GetAllUserStrategyQuery, CandlesUseCase, NoErrorStrategyPort {
    private val methodCallLogs = mutableListOf<String>()

    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        methodCallLogs.add("GetAllUserStrategyQuery.getAllUserStrategies")
        return listOf(
            userStrategyKey("id1", productPriceKey("BTCUSDT", Duration.ofMinutes(1))),
        )
    }

    override suspend fun setCandles(productPriceKey: ProductPriceKey): Candles {
        methodCallLogs.add("CandlesUseCase.setCandles")
        return DEFAULT_CHART_FACTORY.candles()
    }

    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles {
        methodCallLogs.add("CandlesUseCase.addCandles")
        return DEFAULT_CHART_FACTORY.candles()
    }

    override fun removeCandles(productPriceKey: ProductPriceKey) {
        methodCallLogs.add("CandlesUseCase.removeCandles")
    }

    override fun setStrategy(key: UserStrategyKey, strategy: Strategy) {
        methodCallLogs.add("StrategyPort.setStrategy")
    }

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = InitController(
            RunnableStrategyCommandService(
                candlesUseCase = this@InitControllerTest,
                strategyPort = this@InitControllerTest
            ),
            userStrategyQuery = this@InitControllerTest,
        )

        controller.init()

        assertEquals(
            listOf(
                "GetAllUserStrategyQuery.getAllUserStrategies",
                "CandlesUseCase.setCandles",
                "StrategyPort.setStrategy"
            ),
            methodCallLogs
        )
    }
}

interface NoErrorStrategyPort : StrategyPort {
    override fun removeStrategy(key: UserStrategyKey) {
        TODO("Not yet implemented")
    }

    override fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy> {
        TODO("Not yet implemented")
    }

    override fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean {
        TODO("Not yet implemented")
    }
}