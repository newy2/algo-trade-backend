package com.newy.algotrade.unit.price2.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SetUserStrategyControllerTest : SetCandlesUseCase, SetStrategyUseCase {
    private var log: String = ""

    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        log += "setCandles "
    }

    override fun setStrategy(key: UserStrategyKey) {
        log += "setStrategy "
    }

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = SetUserStrategyController(
            this@SetUserStrategyControllerTest,
            this@SetUserStrategyControllerTest
        )

        val key = userStrategyKey("id1", productPriceKey("BTCUSDT"))
        controller.setUserStrategy(key)

        Assertions.assertEquals("setCandles setStrategy ", log)
    }
}