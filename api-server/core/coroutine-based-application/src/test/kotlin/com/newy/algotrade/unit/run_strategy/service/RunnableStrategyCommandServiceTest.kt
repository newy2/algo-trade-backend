package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.StrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RunnableStrategyCommandServiceTest : SetCandlesUseCase, NoErrorStrategyUseCase {
    private val methodCallLogs = mutableListOf<String>()

    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        methodCallLogs.add("SetCandlesUseCase.setCandles")
    }

    override fun setStrategy(key: UserStrategyKey) {
        methodCallLogs.add("StrategyUseCase.setStrategy")
    }

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val service = RunnableStrategyCommandService(
            candlesUseCase = this@RunnableStrategyCommandServiceTest,
            strategyUseCase = this@RunnableStrategyCommandServiceTest,
        )

        val key = userStrategyKey("id1", productPriceKey("BTCUSDT"))
        service.setRunnableStrategy(key)

        assertEquals(
            listOf(
                "SetCandlesUseCase.setCandles",
                "StrategyUseCase.setStrategy"
            ),
            methodCallLogs
        )
    }
}

interface NoErrorStrategyUseCase : StrategyUseCase {
    override fun removeStrategy(key: UserStrategyKey) {
        TODO("Not yet implemented")
    }
}