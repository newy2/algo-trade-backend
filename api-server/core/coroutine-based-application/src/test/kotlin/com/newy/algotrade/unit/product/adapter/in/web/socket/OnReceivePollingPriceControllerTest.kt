package com.newy.algotrade.unit.product.adapter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.RunStrategyUseCase
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class OnReceivePollingPriceControllerTest : AddCandlesUseCase, RunStrategyUseCase {
    private var log: String = ""

    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles {
        log += "addCandles "
        return DEFAULT_CHART_FACTORY.candles()
    }

    override suspend fun runStrategy(productPriceKey: ProductPriceKey) {
        log += "run "
    }


    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = OnReceivePollingPriceController(
            this@OnReceivePollingPriceControllerTest,
            this@OnReceivePollingPriceControllerTest,
        )
        val productPriceKey = ProductPriceKey(Market.BY_BIT, ProductType.SPOT, "BTCUSDT", Duration.ofMinutes(1))
        val productPriceList = emptyList<ProductPrice>()

        controller.onReceivePrice(productPriceKey, productPriceList)

        assertEquals("addCandles run ", log)
    }
}