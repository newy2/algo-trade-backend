package com.newy.algotrade.unit.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.CreateStrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategy
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertTrue

private fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)

private val userStrategyKey = UserStrategyKey(
    "user1",
    StrategyId.BuyTripleRSIStrategy,
    productPriceKey("BTCUSDT", Duration.ofMinutes(1))
)

class SetStrategyUseCaseTest : CreateStrategyPort {
    private var addedCount = 0
    private lateinit var strategy: Strategy

    override fun add(key: UserStrategyKey, strategy: Strategy) {
        this.strategy = strategy
        addedCount++
    }

    @BeforeEach
    fun setUp() {
        addedCount = 0
    }

    @Test
    fun `등록하기`() {
        val service = SetStrategyUseCase(InMemoryCandleStore(), this)

        service.setStrategy(userStrategyKey)

        Assertions.assertEquals(1, addedCount)
        assertTrue(strategy is BuyTripleRSIStrategy)
    }
}