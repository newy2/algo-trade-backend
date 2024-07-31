package com.newy.algotrade.unit.product.service.candle

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceService
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration

@DisplayName("port 호출 순서 확인")
class FetchProductPriceServiceTest : NoErrorProductPriceQueryAdapter, SubscribablePollingProductPricePort {
    private val methodCallLogs = mutableListOf<String>()
    private val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
    private val service = FetchProductPriceService(
        productPricePort = this,
        pollingProductPricePort = this,
    )

    override suspend fun subscribe(key: ProductPriceKey) {
        methodCallLogs.add("subscribe")
    }

    override fun unSubscribe(key: ProductPriceKey) {
        methodCallLogs.add("unSubscribe")
    }

    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> =
        super.getProductPrices(param).also {
            methodCallLogs.add("getProductPrices")
        }

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    fun `fetchInitProductPrices - port 호출 순서 확인`() = runTest {
        service.fetchInitProductPrices(productPriceKey)

        assertEquals(listOf("getProductPrices"), methodCallLogs)
    }

    @Test
    fun `requestPollingProductPrice - port 호출 순서 확인`() = runTest {
        service.requestPollingProductPrice(productPriceKey)

        assertEquals(listOf("subscribe"), methodCallLogs)
    }

    @Test
    fun `requestUnPollingProductPrice - port 호출 순서 확인`() = runTest {
        service.requestUnPollingProductPrice(productPriceKey)

        assertEquals(listOf("unSubscribe"), methodCallLogs)
    }
}

private interface NoErrorProductPriceQueryAdapter : ProductPriceQueryPort {
    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> = emptyList()
}