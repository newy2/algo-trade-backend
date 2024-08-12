package com.newy.algotrade.unit.back_testing.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistence.LoadBackTestingDataAdapter
import com.newy.algotrade.coroutine_based_application.back_testing.port.out.FindBackTestingDataPort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.domain.product_price.ProductPriceKey
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class LoadBackTestingDataAdapterTest : FindBackTestingDataPort, OnReceivePollingPricePort {
    private val interval = Duration.ofMinutes(1)
    private val fakeKey = BackTestingDataKey(
        productPriceKey("BTCUSDT", interval),
        OffsetDateTime.parse("2024-05-01T00:00Z"),
        OffsetDateTime.parse("2024-05-01T00:03Z")
    )
    private val loader = LoadBackTestingDataAdapter(
        fakeKey,
        this,
        this
    )
    private val fakeParam = GetProductPriceHttpParam(
        fakeKey.productPriceKey,
        OffsetDateTime.parse("2024-05-01T00:03Z"),
        limit = 2
    )

    private lateinit var receiveDataList: MutableList<ProductPrice>

    override suspend fun findBackTestingData(key: BackTestingDataKey): List<ProductPrice> {
        return listOf(
            productPrice(100, interval, OffsetDateTime.parse("2024-05-01T00:00Z")),
            productPrice(200, interval, OffsetDateTime.parse("2024-05-01T00:01Z")),
            productPrice(300, interval, OffsetDateTime.parse("2024-05-01T00:02Z")),
            productPrice(400, interval, OffsetDateTime.parse("2024-05-01T00:03Z")),
            productPrice(500, interval, OffsetDateTime.parse("2024-05-01T00:04Z")),
        )
    }

    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        receiveDataList.add(productPriceList.first())
    }

    @BeforeEach
    fun setUp() {
        receiveDataList = mutableListOf()
    }

    @Test
    fun getProductPrices() = runTest {
        val firstDataList = loader.fetchProductPrices(fakeParam)

        assertEquals(
            listOf(
                productPrice(100, interval, OffsetDateTime.parse("2024-05-01T00:00Z")),
                productPrice(200, interval, OffsetDateTime.parse("2024-05-01T00:01Z")),
            ),
            firstDataList
        )
    }

    @Test
    fun await() = runTest {
        loader.fetchProductPrices(fakeParam)

        loader.await()

        assertEquals(
            listOf(
                productPrice(300, interval, OffsetDateTime.parse("2024-05-01T00:02Z")),
                productPrice(400, interval, OffsetDateTime.parse("2024-05-01T00:03Z")),
                productPrice(500, interval, OffsetDateTime.parse("2024-05-01T00:04Z")),
            ),
            receiveDataList
        )
    }
}