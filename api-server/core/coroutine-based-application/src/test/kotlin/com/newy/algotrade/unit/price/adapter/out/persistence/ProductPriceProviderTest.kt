package com.newy.algotrade.unit.price.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.price.domain.ProductPriceProvider
import com.newy.algotrade.coroutine_based_application.price.domain.model.ProductPriceKey
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val now = ZonedDateTime.parse("2024-05-01T00:00Z")

private fun productPrice(amount: Int, interval: Duration) =
    Candle.TimeFrame.from(interval)!!(
        now,
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        0.toBigDecimal(),
    )

fun providerKey(key: String, productCode: String, interval: Duration) =
    ProductPriceProvider.Key(key, productPriceKey(productCode, interval))

fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)

@DisplayName("상품 가격 초기 데이터 로드 listener 테스트")
class OnLoadInitDataListenerTest : LoadProductPricePort, ProductPriceProvider.Listener {
    private var apiCallCount = 0
    private var listenerCallCount = 0
    private lateinit var provider: ProductPriceProvider
    private lateinit var listener: ProductPriceProvider.Listener

    @BeforeEach
    fun setUp() {
        apiCallCount = 0
        listenerCallCount = 0
        provider = ProductPriceProvider(loader = this, initDataSize = 1)
        listener = this
    }

    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        apiCallCount++
        return emptyList()
    }

    override suspend fun onLoadInitData(prices: List<ProductPrice>) {
        listenerCallCount++
    }

    override suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice) {
        TODO("Not yet implemented")
    }

    @Test
    fun `등록된 리스너가 없는 경우`() = runBlocking {
        provider.loadInitData()

        assertEquals(0, apiCallCount)
        assertEquals(0, listenerCallCount)
    }

    @Test
    fun `리스너를 1개만 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)

        provider.loadInitData()

        assertEquals(1, apiCallCount)
        assertEquals(1, listenerCallCount)
    }

    @Test
    fun `같은 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)), listener)

        provider.loadInitData()

        assertEquals(1, apiCallCount)
        assertEquals(2, listenerCallCount)
    }

    @Test
    fun `시간값이 다른 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "BTCUSDT", Duration.ofMinutes(5)), listener)

        provider.loadInitData()

        assertEquals(2, apiCallCount)
        assertEquals(2, listenerCallCount)
    }

    @Test
    fun `다른 시장 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)), listener)

        provider.loadInitData()

        assertEquals(2, apiCallCount)
        assertEquals(2, listenerCallCount)
    }

    @Test
    fun `리스너 등록후 삭제한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)), listener)
        provider.removeListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)))

        provider.loadInitData()

        assertEquals(1, apiCallCount)
        assertEquals(1, listenerCallCount)
    }
}

@DisplayName("상품 가격 업데이트 listener 테스트")
class OnUpdatePriceListenerTest : LoadProductPricePort, ProductPriceProvider.Listener {
    private var listenerCallCount = 0
    private lateinit var provider: ProductPriceProvider
    private lateinit var listener: ProductPriceProvider.Listener

    @BeforeEach
    fun setUp() {
        listenerCallCount = 0
        provider = ProductPriceProvider(loader = this, initDataSize = 1)
        listener = this
    }

    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        return emptyList()
    }

    override suspend fun onLoadInitData(prices: List<ProductPrice>) {
        TODO("Not yet implemented")
    }

    override suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice) {
        listenerCallCount++
    }

    @Test
    fun `등록된 리스너가 없는 경우`() = runBlocking {
        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(0, listenerCallCount)
    }

    @Test
    fun `리스너를 1개만 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)

        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(1, listenerCallCount)
    }

    @Test
    fun `같은 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)), listener)

        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(2, listenerCallCount)
    }

    @Test
    fun `시간값이 다른 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "BTCUSDT", Duration.ofMinutes(5)), listener)

        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(1, listenerCallCount)
    }

    @Test
    fun `다른 시장 상품에 대한 리스너를 여러 개 등록한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)), listener)

        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(1, listenerCallCount)
    }

    @Test
    fun `리스너 등록후 삭제한 경우`() = runBlocking {
        provider.putListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)), listener)
        provider.putListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)), listener)
        provider.removeListener(providerKey("key2", "삼성전자", Duration.ofMinutes(1)))

        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(1, listenerCallCount)
    }
}

@DisplayName("notify 된 상품 가격 확인 테스트")
class ProductPriceProviderTest : LoadProductPricePort, ProductPriceProvider.Listener {
    private lateinit var provider: ProductPriceProvider
    private var loadedInitPrices: List<ProductPrice> = emptyList()
    private var updatePrice: ProductPrice? = null

    @BeforeEach
    fun setUp() {
        provider = ProductPriceProvider(loader = this, initDataSize = 1).also {
            it.putListener(
                providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)),
                this
            )
        }
        loadedInitPrices = emptyList()
        updatePrice = null
    }

    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        return listOf(productPrice(1000, Duration.ofMinutes(1)))
    }

    override suspend fun onLoadInitData(prices: List<ProductPrice>) {
        loadedInitPrices = prices
    }

    override suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice) {
        updatePrice = price
    }

    @Test
    fun `등록한 상품의 초기 데이터 로드가 완료된 경우`() = runBlocking {
        provider.loadInitData()

        assertEquals(listOf(productPrice(1000, Duration.ofMinutes(1))), loadedInitPrices)
    }

    @Test
    fun `등록한 상품의 가격정보가 업데이트 된 경우`() = runBlocking {
        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )

        assertEquals(productPrice(1000, Duration.ofMinutes(1)), updatePrice)
    }

    @Test
    fun `다른 상품의 가격정보가 업데이트 된 경우`() = runBlocking {
        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(5)),
            productPrice(1000, Duration.ofMinutes(5))
        )

        assertNull(updatePrice)
    }

    @Test
    fun `ProductPriceKey 의 interval 과 ProductPrice dml interval 이 다른 경우`() = runBlocking {
        try {
            provider.updatePrice(
                productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
                productPrice(1000, Duration.ofMinutes(5)),
            )
            fail("")
        } catch (e: IllegalArgumentException) {
            assertEquals("잘못된 파라미터 입니다.", e.message)
        }
    }
}
