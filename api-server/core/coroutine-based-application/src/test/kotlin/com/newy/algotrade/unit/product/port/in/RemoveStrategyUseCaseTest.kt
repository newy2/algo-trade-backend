package com.newy.algotrade.unit.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.RemoveStrategyUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.RemoveStrategyPort
import com.newy.algotrade.domain.chart.strategy.StrategyId
import helpers.productPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

private val userStrategyKey = UserStrategyKey(
    "user1",
    StrategyId.BuyTripleRSIStrategy,
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