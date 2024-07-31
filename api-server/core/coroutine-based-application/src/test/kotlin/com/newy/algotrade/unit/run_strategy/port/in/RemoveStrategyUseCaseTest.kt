package com.newy.algotrade.unit.run_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RemoveStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.RemoveStrategyPort
import helpers.productPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

private val userStrategyKey = UserStrategyKey(
    "user1",
    "BuyTripleRSIStrategy",
    productPriceKey("BTCUSDT", Duration.ofMinutes(1))
)


class RemoveStrategyUseCaseTest : RemoveStrategyPort {
    private var removedCount = 0

    override fun removeStrategy(key: UserStrategyKey) {
        removedCount++
    }

    @BeforeEach
    fun setUp() {
        removedCount = 0
    }

    @Test
    fun `해제하기`() {
        val service = RemoveStrategyUseCase(this)

        service.removeStrategy(userStrategyKey)

        Assertions.assertEquals(1, removedCount)
    }
}