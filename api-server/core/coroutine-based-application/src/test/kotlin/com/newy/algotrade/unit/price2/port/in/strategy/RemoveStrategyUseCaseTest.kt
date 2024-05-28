package com.newy.algotrade.unit.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.RemoveStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteStrategyPort
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

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

class RemoveStrategyUseCaseTest : DeleteStrategyPort {
    private var removedCount = 0

    override fun remove(key: UserStrategyKey) {
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