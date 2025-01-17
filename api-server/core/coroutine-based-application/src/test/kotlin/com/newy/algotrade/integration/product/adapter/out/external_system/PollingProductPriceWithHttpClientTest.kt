package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.external_system.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system.FetchByBitProductPrice
import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system.FetchProductPriceProxyAdapter
import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system.PollingProductPriceWithHttpClient
import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.product_price.ProductPriceKey
import helpers.BaseDisabledTest
import helpers.TestEnv
import helpers.productPriceKey
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

class PollingProductPriceTestHelper(
    loader: ProductPricePort,
    delayMillis: Long,
    coroutineContext: CoroutineContext,
    pollingCallback: PollingCallback<ProductPriceKey, List<ProductPrice>>
) : PollingProductPriceWithHttpClient(
    loader = loader,
    delayMillis = delayMillis,
    coroutineContext = coroutineContext,
    pollingCallback = pollingCallback
) {
    override fun endTime(): OffsetDateTime {
        return OffsetDateTime.parse("2024-05-09T00:00+09:00")
    }

    override fun limit(): Int {
        return 2
    }
}

@DisplayName("ByBit 폴링 HTTP API")
class PollingProductPriceWithByBitWebSocketTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.ByBit.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val api = FetchProductPriceProxyAdapter(
        mapOf(
            Market.BY_BIT to FetchByBitProductPrice(client)
        )
    )

    @Test
    fun `바이빗 가격정보 폴링`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, ProductPrice>>()
        var index = 0

        val pollingJob = PollingProductPriceTestHelper(api, delayMillis = 1000, coroutineContext) { (key, list) ->
            channel.send(Pair(key, list[index++])) // 실시간 API 흉내를 내기 위해서, index 사용
        }

        pollingJob.start()
        pollingJob.subscribe(
            productPriceKey(
                productCode = "BTCUSDT",
                interval = Duration.ofMinutes(1)
            )
        )


        var receiveProductPriceKey: ProductPriceKey? = null
        val receiveProductPrices = mutableListOf<ProductPrice>()
        val watcher = launch {
            while (isActive) {
                val (key, value) = channel.receive()
                receiveProductPriceKey = key
                receiveProductPrices.add(value)
                if (receiveProductPrices.size == 2) {
                    pollingJob.cancel()
                    cancel()
                }
            }
        }
        watcher.join()

        assertEquals(
            productPriceKey(
                productCode = "BTCUSDT",
                interval = Duration.ofMinutes(1)
            ),
            receiveProductPriceKey
        )
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("2024-05-08T14:59Z"),
                    openPrice = 60379.25.toBigDecimal(),
                    highPrice = 60379.31.toBigDecimal(),
                    lowPrice = 60379.23.toBigDecimal(),
                    closePrice = 60379.26.toBigDecimal(),
                    volume = 0.156321.toBigDecimal()
                ),
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("2024-05-08T15:00Z"),
                    openPrice = 60379.26.toBigDecimal(),
                    highPrice = 60379.29.toBigDecimal(),
                    lowPrice = 60252.4.toBigDecimal(),
                    closePrice = 60379.28.toBigDecimal(),
                    volume = 0.133579.toBigDecimal()
                )
            ), receiveProductPrices
        )
    }
}

@DisplayName("LS증권 폴링 HTTP API")
class PollingLsSecProductPriceTest : BaseDisabledTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.LsSec.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val accessTokenLoader = LsSecAccessTokenHttpApi(client)
    private val api = FetchProductPriceProxyAdapter(
        mapOf(
            Market.LS_SEC to FetchLsSecProductPrice(
                client,
                accessTokenLoader,
                PrivateApiInfo(
                    key = TestEnv.LsSec.apiKey,
                    secret = TestEnv.LsSec.apiSecret,
                )
            )
        )
    )

    @DisabledIf("hasNotLsSecApiInfo")
    @Test
    fun `LS증권 가격정보 폴링`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, ProductPrice>>()
        var index = 0

        val pollingJob = PollingProductPriceTestHelper(api, delayMillis = 1000, coroutineContext) { (key, list) ->
            channel.send(Pair(key, list[index++])) // 실시간 API 흉내를 내기 위해서, index 사용
        }

        pollingJob.start()
        pollingJob.subscribe(
            productPriceKey(
                productCode = "078020",
                interval = Duration.ofMinutes(1),
            )
        )

        var receiveProductPriceKey: ProductPriceKey? = null
        val receiveProductPrices = mutableListOf<ProductPrice>()
        val watcher = launch {
            while (isActive) {
                val (key, value) = channel.receive()
                receiveProductPriceKey = key
                receiveProductPrices.add(value)
                if (receiveProductPrices.size == 2) {
                    pollingJob.cancel()
                    cancel()
                }
            }
        }
        watcher.join()

        assertEquals(
            productPriceKey(
                productCode = "078020",
                interval = Duration.ofMinutes(1),
            ),
            receiveProductPriceKey
        )
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("2024-05-09T15:19+09"),
                    openPrice = 4925.0.toBigDecimal(),
                    highPrice = 4925.0.toBigDecimal(),
                    lowPrice = 4925.0.toBigDecimal(),
                    closePrice = 4925.0.toBigDecimal(),
                    volume = 3.0.toBigDecimal()
                ),
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("2024-05-09T15:29+09"),
                    openPrice = 4870.0.toBigDecimal(),
                    highPrice = 4870.0.toBigDecimal(),
                    lowPrice = 4870.0.toBigDecimal(),
                    closePrice = 4870.0.toBigDecimal(),
                    volume = 1152.0.toBigDecimal()
                )
            ), receiveProductPrices
        )
    }
}