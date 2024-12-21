package com.newy.algotrade.unit.back_testing.adapter.`in`.internal_system

import com.newy.algotrade.back_testing.adapter.`in`.internal_system.OnReceivePollingPriceController
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.RunStrategyResult
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class OnReceivePollingPriceControllerTest {
    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val methodCallLogs = mutableListOf<String>()
        val controller = OnReceivePollingPriceController(
            addCandlesUseCase = { _, _ ->
                DEFAULT_CHART_FACTORY.candles().also {
                    methodCallLogs.add("addCandlesUseCase")
                }
            },
            runStrategyUseCase = {
                RunStrategyResult().also {
                    methodCallLogs.add("runStrategyUseCase")
                }
            }
        )
        val productPriceKey = ProductPriceKey(Market.BY_BIT, ProductType.SPOT, "BTCUSDT", Duration.ofMinutes(1))
        val productPriceList = emptyList<ProductPrice>()

        controller.onReceivePrice(productPriceKey, productPriceList)

        assertEquals(listOf("addCandlesUseCase", "runStrategyUseCase"), methodCallLogs)
    }
}