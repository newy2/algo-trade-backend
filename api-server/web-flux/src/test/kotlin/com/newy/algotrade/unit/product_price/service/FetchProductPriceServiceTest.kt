package com.newy.algotrade.unit.product_price.service

import com.newy.algotrade.common.extension.ProductPrice
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.ProductPricePort
import com.newy.algotrade.product_price.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.product_price.service.ProductPriceQueryService
import helpers.productPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration

@DisplayName("단순 조회 Service - port 호출 확인")
class FetchProductPriceServiceTest : NoErrorProductPriceAdapter, SubscribablePollingProductPricePort {
    private val methodCallLogs = mutableListOf<String>()
    private val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
    private val service = ProductPriceQueryService(
        productPricePort = this,
        pollingProductPricePort = this,
    )

    override fun subscribe(key: ProductPriceKey) {
        methodCallLogs.add("subscribe")
    }

    override fun unSubscribe(key: ProductPriceKey) {
        methodCallLogs.add("unSubscribe")
    }

    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> =
        super.fetchProductPrices(param).also {
            methodCallLogs.add("getProductPrices")
        }

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    fun `fetchInitProductPrices - port 호출 확인`() = runTest {
        service.getInitProductPrices(productPriceKey)

        assertEquals(listOf("getProductPrices"), methodCallLogs)
    }

    @Test
    fun `requestPollingProductPrice - port 호출 확인`() = runTest {
        service.requestPollingProductPrice(productPriceKey)

        assertEquals(listOf("subscribe"), methodCallLogs)
    }

    @Test
    fun `requestUnPollingProductPrice - port 호출 확인`() = runTest {
        service.requestUnPollingProductPrice(productPriceKey)

        assertEquals(listOf("unSubscribe"), methodCallLogs)
    }
}

private interface NoErrorProductPriceAdapter : ProductPricePort {
    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> = emptyList()
}