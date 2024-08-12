package com.newy.algotrade.unit.product_price.adapter.`in`.internal_system

import com.newy.algotrade.coroutine_based_application.product_price.adapter.`in`.internal_system.InitController
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import helpers.productPriceKey
import helpers.userStrategyKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InitControllerTest {
    private val methodCallLogs = mutableListOf<String>()
    private val userStrategyKeys = mutableListOf<UserStrategyKey>()
    private val controller = InitController(
        setRunnableStrategyUseCase = { methodCallLogs.add("setRunnableStrategyUseCase") },
        getAllUserStrategyProductQuery = {
            userStrategyKeys.also {
                methodCallLogs.add("getAllUserStrategyProductQuery")
            }
        }
    )

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
        userStrategyKeys.clear()
    }

    @Test
    fun `userStrategyProduct 가 없는 경우`() = runTest {
        controller.init()

        assertEquals(listOf("getAllUserStrategyProductQuery"), methodCallLogs)
    }

    @Test
    fun `userStrategyProduct 가 1개 인 경우`() = runTest {
        userStrategyKeys.add(userStrategyKey(userStrategyId = 1, productPriceKey = productPriceKey("BTCUSDT")))

        controller.init()

        assertEquals(
            listOf(
                "getAllUserStrategyProductQuery",
                "setRunnableStrategyUseCase",
            ),
            methodCallLogs
        )
    }

    @Test
    fun `userStrategyProduct 가 여러개 인 경우`() = runTest {
        userStrategyKeys.add(userStrategyKey(userStrategyId = 1, productPriceKey = productPriceKey("BTCUSDT")))
        userStrategyKeys.add(userStrategyKey(userStrategyId = 1, productPriceKey = productPriceKey("ETHUSDT")))
        userStrategyKeys.add(userStrategyKey(userStrategyId = 2, productPriceKey = productPriceKey("BTCUSDT")))

        controller.init()

        assertEquals(
            listOf(
                "getAllUserStrategyProductQuery",
                "setRunnableStrategyUseCase",
                "setRunnableStrategyUseCase",
                "setRunnableStrategyUseCase",
            ),
            methodCallLogs
        )
    }
}