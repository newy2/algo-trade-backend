package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesQueryService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.product.ProductPriceKey
import helpers.productPriceKey
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("port 호출 확인 테스트")
class CandlesQueryServiceTest : NoErrorCandleQueryPort {
    private val methodCallLogs = mutableListOf<String>()

    @Test
    fun `port 호출 확인`() {
        val service = CandlesQueryService(this)

        service.getCandles(productPriceKey("BTCUSDT"))

        assertEquals(listOf("CandleQueryPort.getCandles"), methodCallLogs)
    }

    override fun getCandles(key: ProductPriceKey): Candles =
        super.getCandles(key).also {
            methodCallLogs.add("CandleQueryPort.getCandles")
        }
}

private interface NoErrorCandleQueryPort : CandleQueryPort {
    override fun getCandles(key: ProductPriceKey): Candles =
        DEFAULT_CHART_FACTORY.candles()

    override fun hasCandles(key: ProductPriceKey): Boolean =
        true
}