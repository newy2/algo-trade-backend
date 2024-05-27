package com.newy.algotrade.unit.price2.adapter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunUserStrategyUseCase
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class OnReceivePollingPriceControllerTest : AddCandleUseCase, RunUserStrategyUseCase {
    private var log: String = ""

    override fun addCandle(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles {
        log += "addCandle "
        return DEFAULT_CHART_FACTORY.candles()
    }

    override fun run(productPriceKey: ProductPriceKey) {
        log += "run "
    }


    @Test
    fun `UseCase 호출 순서 확인`() {
        val controller = OnReceivePollingPriceController(this, this)
        val productPriceKey = ProductPriceKey(Market.BY_BIT, ProductType.SPOT, "BTCUSDT", Duration.ofMinutes(1))
        val productPriceList = emptyList<ProductPrice>()

        controller.onReceivePrice(productPriceKey, productPriceList)

        assertEquals("addCandle run ", log)
    }
}