package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.AddStrategyPort
import com.newy.algotrade.coroutine_based_application.product.service.SetStrategyService
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategy
import helpers.productPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertTrue

private val userStrategyKey = UserStrategyKey(
    "user1",
    "BuyTripleRSIStrategy",
    productPriceKey("BTCUSDT", Duration.ofMinutes(1))
)

class SetStrategyServiceTest : AddStrategyPort {
    private var addedCount = 0
    private lateinit var strategy: Strategy

    override fun addStrategy(key: UserStrategyKey, strategy: Strategy) {
        this.strategy = strategy
        addedCount++
    }

    @BeforeEach
    fun setUp() {
        addedCount = 0
    }

    @Test
    fun `등록하기`() {
        val service = SetStrategyService(InMemoryCandleStore(), this)

        service.setStrategy(userStrategyKey)

        Assertions.assertEquals(1, addedCount)
        assertTrue(strategy is BuyTripleRSIStrategy)
    }
}