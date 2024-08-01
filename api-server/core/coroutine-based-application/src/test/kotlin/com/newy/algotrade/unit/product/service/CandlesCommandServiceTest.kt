package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.coroutine_based_application.product.service.CandlesCommandService
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

@DisplayName("port 호출 순서 확인")
class CandlesUseCaseTest : NoErrorCandlePort, NoErrorFetchProductPriceQuery {
    private val methodCallLogs = mutableListOf<String>()
    private val service: CandlesUseCase = CandlesCommandService(
        fetchProductPriceQuery = this,
        candlePort = this,
    )
    private val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    fun `setCandles - port 호출 순서 확인`() = runTest {
        service.setCandles(productPriceKey)

        assertEquals(
            listOf(
                "CandlePort.hasCandles",
                "FetchProductPriceQuery.fetchInitProductPrices",
                "CandlePort.setCandles",
                "FetchProductPriceQuery.requestPollingProductPrice",
            ),
            methodCallLogs
        )
    }

    @Test
    fun `addCandles - port 호출 순서 확인`() = runTest {
        service.addCandles(productPriceKey, emptyList())

        assertEquals(listOf("CandlePort.addCandles"), methodCallLogs)
    }

    override suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice> =
        super.fetchInitProductPrices(productPriceKey).also {
            methodCallLogs.add("FetchProductPriceQuery.fetchInitProductPrices")
        }

    override fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {
        methodCallLogs.add("FetchProductPriceQuery.requestPollingProductPrice")
    }

    override fun hasCandles(key: ProductPriceKey): Boolean =
        super.hasCandles(key).also {
            methodCallLogs.add("CandlePort.hasCandles")
        }

    override fun addCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles =
        super.addCandles(key, list).also {
            methodCallLogs.add("CandlePort.addCandles")
        }

    override fun setCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles =
        super.setCandles(key, list).also {
            methodCallLogs.add("CandlePort.setCandles")
        }
}


@DisplayName("캔들 초기 데이터 저장 테스트")
class SetCandlesServiceTest2 : ProductPriceQueryPort, NoErrorSubscribablePollingProductPriceAdapter {
    private var apiCallCount = 0
    private var pollingSubscribeCount = 0
    private lateinit var service: CandlesUseCase

    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        apiCallCount++
        return listOf(
            productPrice(1000, param.productPriceKey.interval)
        )
    }

    override fun subscribe(key: ProductPriceKey) {
        pollingSubscribeCount++
    }

    @BeforeEach
    fun setUp() {
        apiCallCount = 0
        pollingSubscribeCount = 0
        service = CandlesCommandService(
            fetchProductPriceQuery = FetchProductPriceService(
                productPricePort = this,
                pollingProductPricePort = this,
            ),
            candlePort = InMemoryCandleStore(),
        )
    }

    @Test
    fun `1개 상품만 등록한 경우`() = runBlocking {
        val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))

        service.setCandles(productPriceKey)

        assertEquals(1, apiCallCount)
        assertEquals(1, pollingSubscribeCount)
    }

    @Test
    fun `같은 상품을 등록한 경우`() = runBlocking {
        val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))

        service.setCandles(productPriceKey)
        service.setCandles(productPriceKey)

        assertEquals(1, apiCallCount)
        assertEquals(2, pollingSubscribeCount, "폴링은 여러번 요청해도 영향 없음")
    }

    @Test
    fun `다른 상품을 등록한 경우`() = runBlocking {
        val productPriceKey1 = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
        val productPriceKey2 = productPriceKey("BTCUSDT", Duration.ofMinutes(5))

        service.setCandles(productPriceKey1)
        service.setCandles(productPriceKey2)

        assertEquals(2, apiCallCount)
        assertEquals(2, pollingSubscribeCount)
    }
}

@DisplayName("캔들 추가 테스트")
class AddCandlesServiceTest2 {
    private val now = OffsetDateTime.parse("2024-05-01T00:00Z")
    private val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
    private val productPriceList = listOf(
        productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0)),
        productPrice(2000, Duration.ofMinutes(1), now.plusMinutes(1)),
    )

    private lateinit var nextPrice: ListIterator<ProductPrice>
    private lateinit var candleStore: CandlePort
    private lateinit var service: AddCandlesUseCase

    @BeforeEach
    fun setUp() {
        nextPrice = productPriceList.listIterator()
        candleStore = InMemoryCandleStore().also {
            it.setCandles(productPriceKey, listOf(nextPrice.next()))
        }
        service = CandlesCommandService(
            fetchProductPriceQuery = object : NoErrorFetchProductPriceQuery {},
            candlePort = candleStore,
        )
    }

    @Test
    fun `candles 업데이트 하는 경우`() {
        val candles = service.addCandles(productPriceKey, listOf(nextPrice.next()))

        candles.let {
            Assertions.assertEquals(2, it.size)
            Assertions.assertEquals(productPriceList[0], it[0])
            Assertions.assertEquals(productPriceList[1], it[1])
        }
    }

    @Test
    fun `제거된 candles 업데이트 하는 경우 - 업데이트가 무시된다`() {
        candleStore.removeCandles(productPriceKey)

        val candles = service.addCandles(productPriceKey, listOf(nextPrice.next()))

        Assertions.assertEquals(0, candles.size)
    }
}

private interface NoErrorCandlePort : CandlePort {
    override fun getCandles(key: ProductPriceKey): Candles = DEFAULT_CHART_FACTORY.candles()
    override fun hasCandles(key: ProductPriceKey): Boolean = false
    override fun addCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles = DEFAULT_CHART_FACTORY.candles()
    override fun setCandles(key: ProductPriceKey, list: List<ProductPrice>): Candles = DEFAULT_CHART_FACTORY.candles()
    override fun removeCandles(key: ProductPriceKey) {}
}

private interface NoErrorFetchProductPriceQuery : FetchProductPriceQuery {
    override fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey) {}
    override fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {}
    override suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice> = emptyList()
}

interface NoErrorSubscribablePollingProductPriceAdapter : SubscribablePollingProductPricePort {
    override fun unSubscribe(key: ProductPriceKey) {
        TODO("Not yet implemented")
    }
}