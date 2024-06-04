package com.newy.algotrade.unit.price2.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.system.InitController
import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.user_strategy.GetAllUserStrategyQuery
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration

class InitControllerTest : GetAllUserStrategyQuery, SetCandlesUseCase, SetStrategyUseCase {
    private var log: String = ""
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        log += "getAllUserStrategies "
        return listOf(
            userStrategyKey("id1", productPriceKey("BTCUSDT", Duration.ofMinutes(1))),
            userStrategyKey("id2", productPriceKey("BTCUSDT", Duration.ofMinutes(1))),
        )
    }

    override suspend fun setCandles(productPriceKey: ProductPriceKey) {
        log += "setCandles "
    }

    override fun setStrategy(key: UserStrategyKey) {
        log += "setStrategy "
    }

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = InitController(
            SetUserStrategyController(this@InitControllerTest, this@InitControllerTest),
            this@InitControllerTest,
        )

        controller.init()

        Assertions.assertEquals("getAllUserStrategies setCandles setStrategy setCandles setStrategy ", log)
    }
}