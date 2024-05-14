package com.newy.algotrade.unit.price.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.price.domain.ProductPriceProvider
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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

open class NullListenerForTestHelper(
    override val callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit = {}
) : LoadProductPricePort,
    ProductPriceProvider.Listener,
    Polling<ProductPriceKey, List<ProductPrice>> {
    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> = emptyList()
    override suspend fun start() {}
    override fun cancel() {}
    override fun unSubscribe(key: ProductPriceKey) {}
    override suspend fun subscribe(key: ProductPriceKey) {}
    override suspend fun onLoadInitData(prices: List<ProductPrice>) {}
    override suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice) {}
}

@DisplayName("ProductPriceProvider 초기화 테스트")
class InitProductPriceProviderTest : NullListenerForTestHelper() {
    private lateinit var provider: ProductPriceProvider
    private var apiCallCount = 0
    private var pollingSubscribeCount = 0
    private var listenerCallCount = 0
    private var log = ""

    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        apiCallCount++
        log += "productPrices -> "
        return emptyList()
    }

    override suspend fun onLoadInitData(prices: List<ProductPrice>) {
        listenerCallCount++
        log += "onLoadInitData -> "
    }

    override suspend fun subscribe(key: ProductPriceKey) {
        pollingSubscribeCount++
        log += "subscribe | "
    }

    @BeforeEach
    fun setUp() {
        provider = ProductPriceProvider(
            initDataLoader = this,
            pollingDataLoader = this,
            initDataSize = 1,
        )
        apiCallCount = 0
        pollingSubscribeCount = 0
        listenerCallCount = 0
        log = ""
    }

    @Test
    fun `리스너를 1개로 초기화 하는 경우`() = runBlocking {
        provider.init(
            providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this@InitProductPriceProviderTest,
        )

        assertEquals(1, apiCallCount)
        assertEquals(1, pollingSubscribeCount)
        assertEquals(1, listenerCallCount)
        assertEquals("productPrices -> onLoadInitData -> subscribe | ", log)
    }

    @Test
    fun `같은 상품에 대한 2개의 리스너로 초기화 하는 경우`() = runBlocking {
        provider.init(
            providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this@InitProductPriceProviderTest,
            providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)) to this@InitProductPriceProviderTest,
        )

        assertEquals(1, apiCallCount)
        assertEquals(1, pollingSubscribeCount)
        assertEquals(2, listenerCallCount)
        assertEquals("productPrices -> onLoadInitData -> onLoadInitData -> subscribe | ", log)
    }

    @Test
    fun `다른 상품에 대한 2개의 리스너로 초기화 하는 경우`() = runBlocking {
        provider.init(
            providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this@InitProductPriceProviderTest,
            providerKey("key2", "BTCUSDT", Duration.ofMinutes(5)) to this@InitProductPriceProviderTest,
        )

        assertEquals(2, apiCallCount)
        assertEquals(2, pollingSubscribeCount)
        assertEquals(2, listenerCallCount)
        assertEquals(
            "productPrices -> onLoadInitData -> subscribe | productPrices -> onLoadInitData -> subscribe | ",
            log
        )
    }
}

@DisplayName("상품 가격 업데이트 listener 테스트")
class OnUpdatePriceListenerTest : LoadProductPricePort, NullListenerForTestHelper() {
    private lateinit var provider: ProductPriceProvider
    private var listenerCallCount = 0

    override suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice) {
        listenerCallCount++
    }

    private suspend fun setUp() {
        provider = ProductPriceProvider(
            initDataLoader = this,
            pollingDataLoader = this,
            initDataSize = 1
        ).also {
            it.init(
                providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this@OnUpdatePriceListenerTest,
                providerKey("key2", "BTCUSDT", Duration.ofMinutes(5)) to this@OnUpdatePriceListenerTest,
            )
        }
    }

    private suspend fun updatePrice() {
        provider.updatePrice(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1)),
            productPrice(1000, Duration.ofMinutes(1))
        )
    }

    @Test
    fun `등록한 상품의 가격이 업데이트 된 경우`() = runBlocking {
        setUp()

        updatePrice()

        assertEquals(1, listenerCallCount)
    }

    @Test
    fun `같은 상품에 대한 리스터를 추가한 경우`() = runBlocking {
        setUp()
        provider.putListener(
            providerKey("key3", "BTCUSDT", Duration.ofMinutes(1)), this@OnUpdatePriceListenerTest
        )

        updatePrice()

        assertEquals(2, listenerCallCount)
    }

    @Test
    fun `리스너를 삭제한 경우`() = runBlocking {
        setUp()
        provider.removeListener(providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)))

        updatePrice()

        assertEquals(0, listenerCallCount)
    }
}

@DisplayName("리스너 삭제 테스트")
class UnSubscribeTest : NullListenerForTestHelper() {
    private lateinit var provider: ProductPriceProvider
    private var unSubscribeKey: ProductPriceKey? = null

    override fun unSubscribe(key: ProductPriceKey) {
        unSubscribeKey = key
    }

    private suspend fun setUp() {
        provider = ProductPriceProvider(
            initDataLoader = this,
            pollingDataLoader = this,
            initDataSize = 1
        ).also {
            it.init(
                providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this,
                providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)) to this,
                providerKey("key3", "BTCUSDT", Duration.ofMinutes(5)) to this,
            )
        }
    }

    @Test
    fun `삭제하는 상품을 사용하는 다른 리스너가 있다면, unSubscribe 를 호출하지 않는다`() = runBlocking {
        setUp()

        provider.removeListener(providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)))

        assertNull(unSubscribeKey)
    }

    @Test
    fun `삭제하는 상품을 사용하는 다른 리스너가 없다면, unSubscribe 를 호출한다`() = runBlocking {
        setUp()

        provider.removeListener(providerKey("key3", "BTCUSDT", Duration.ofMinutes(5)))

        assertEquals(unSubscribeKey, productPriceKey("BTCUSDT", Duration.ofMinutes(5)))
    }
}

@DisplayName("리스너 등록 테스트")
class SubscribeTest : NullListenerForTestHelper() {
    private lateinit var provider: ProductPriceProvider
    private var subscribeKey: ProductPriceKey? = null
    private var log = ""

    override suspend fun onLoadInitData(prices: List<ProductPrice>) {
        log += "global@onLoadInitData "
    }

    override suspend fun subscribe(key: ProductPriceKey) {
        log += "subscribe "
        subscribeKey = key
    }

    private suspend fun setUp() {
        provider = ProductPriceProvider(
            initDataLoader = this,
            pollingDataLoader = this,
            initDataSize = 1
        ).also {
            it.init(
                providerKey("key1", "BTCUSDT", Duration.ofMinutes(1)) to this,
            )
        }
        subscribeKey = null
        log = ""
    }

    @Test
    fun `이미 subscribe 하고 있는 상품에 대한 리스너를 추가한다면, subscribe 를 호출하지 않는다`() = runBlocking {
        setUp()

        provider.putListener(
            providerKey("key2", "BTCUSDT", Duration.ofMinutes(1)),
            object : NullListenerForTestHelper() {
                override suspend fun onLoadInitData(prices: List<ProductPrice>) {
                    log += "onLoadInitData "
                }
            })

        assertEquals("onLoadInitData ", log)
        assertNull(subscribeKey)
    }

    @Test
    fun `subscribe 하지 않은 상품을 추가한다면, subscribe 를 호출한다`() = runBlocking {
        setUp()

        provider.putListener(
            providerKey("key2", "BTCUSDT", Duration.ofMinutes(5)),
            object : NullListenerForTestHelper() {
                override suspend fun onLoadInitData(prices: List<ProductPrice>) {
                    log += "onLoadInitData -> "
                }
            })

        assertEquals("onLoadInitData -> subscribe ", log)
        assertEquals(subscribeKey, productPriceKey("BTCUSDT", Duration.ofMinutes(5)))
    }
}