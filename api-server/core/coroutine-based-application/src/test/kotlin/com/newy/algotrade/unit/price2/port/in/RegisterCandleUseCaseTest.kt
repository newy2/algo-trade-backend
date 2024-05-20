package com.newy.algotrade.unit.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RegisterCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

private val now = OffsetDateTime.parse("2024-05-01T00:00Z")

private fun productPrice(amount: Int, interval: Duration) =
    Candle.TimeFrame.from(interval)!!(
        now,
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        0.toBigDecimal(),
    )

fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)

class OnPollingProductPrice(
    private val candlesStore: CandlePort = InMemoryCandleStore()
) {
    fun onPolling(data: Pair<ProductPriceKey, List<ProductPrice>>) {
        candlesStore.addCandles(data.first, data.second)
    }
}

open class NullPollingProductPriceAdapter(
    private val onPollingProductPrice: OnPollingProductPrice = OnPollingProductPrice(),
    override var callback: PollingCallback<ProductPriceKey, List<ProductPrice>>? = { onPollingProductPrice.onPolling(it) }
) : PollingProductPricePort {
    override suspend fun start() {}
    override fun cancel() {}

    override suspend fun subscribe(key: ProductPriceKey) {}

    override fun unSubscribe(key: ProductPriceKey) {}

    override suspend fun onNextTick(key: ProductPriceKey, value: List<ProductPrice>) {
        super.onNextTick(key, value)
    }
}


class RegisterCandleUseCaseTest : GetProductPricePort, NullPollingProductPriceAdapter() {
    private var apiCallCount = 0
    private var pollingSubscribeCount = 0
    private lateinit var service: RegisterCandleUseCase
    private lateinit var candlesStore: CandlePort

    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        apiCallCount++
        return listOf(productPrice(1000, param.productPriceKey.interval))
    }

    override suspend fun subscribe(key: ProductPriceKey) {
        pollingSubscribeCount++
    }

    @BeforeEach
    fun setUp() {
        apiCallCount = 0
        pollingSubscribeCount = 0
        candlesStore = InMemoryCandleStore()
        service = RegisterCandleUseCase(
            getProductPricePort = this,
            pollingProductPricePort = this,
            candlePort = candlesStore,
        )
    }

    @Test
    fun `1개 상품만 등록한 경우`() = runBlocking {
        service.register(productPriceKey("BTCUSDT", Duration.ofMinutes(1)))

        assertEquals(1, apiCallCount)
        assertEquals(1, pollingSubscribeCount)
    }

    @Test
    fun `같은 상품을 등록한 경우`() = runBlocking {
        service.register(productPriceKey("BTCUSDT", Duration.ofMinutes(1)))
        service.register(productPriceKey("BTCUSDT", Duration.ofMinutes(1)))

        assertEquals(1, apiCallCount)
        assertEquals(2, pollingSubscribeCount, "폴링은 여러번 요청해도 영향 없음")
    }

    @Test
    fun `다른 상품을 등록한 경우`() = runBlocking {
        service.register(productPriceKey("BTCUSDT", Duration.ofMinutes(1)))
        service.register(productPriceKey("BTCUSDT", Duration.ofMinutes(5)))

        assertEquals(2, apiCallCount)
        assertEquals(2, pollingSubscribeCount)
    }
}