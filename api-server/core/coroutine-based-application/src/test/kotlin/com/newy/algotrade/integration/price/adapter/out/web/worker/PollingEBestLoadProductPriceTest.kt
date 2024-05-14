package com.newy.algotrade.integration.price.adapter.out.web.worker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.EBestAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.EBestLoadProductPriceHttpApi
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.LoadProductPriceSelector
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.worker.PollingLoadProductPrice
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals


class TestHelper(
    loader: LoadProductPricePort,
    delayMillis: Long,
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit
) : PollingLoadProductPrice(loader, delayMillis, coroutineContext, callback) {
    override fun endTime(): OffsetDateTime {
        return OffsetDateTime.parse("2024-05-09T00:00+09:00")
    }

    override fun limit(): Int {
        return 2
    }
}

class PollingEBestLoadProductPriceTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.EBest.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val accessTokenLoader = EBestAccessTokenHttpApi(client)
    private val api = LoadProductPriceSelector(
        mapOf(
            Market.E_BEST to EBestLoadProductPriceHttpApi(
                client,
                accessTokenLoader,
                PrivateApiInfo(
                    key = TestEnv.EBest.apiKey,
                    secret = TestEnv.EBest.apiSecret,
                )
            )
        )
    )

    @Test
    fun `이베스트 가격정보 폴링`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, ProductPrice>>()
        var index = 0

        val pollingJob = ByBitTestHelper(api, delayMillis = 1000, coroutineContext) { (key, list) ->
            channel.send(Pair(key, list[index++])) // 실시간 API 흉내를 내기 위해서, index 사용
        }

        pollingJob.start()
        pollingJob.subscribe(
            ProductPriceKey(
                Market.E_BEST,
                ProductType.SPOT,
                "078020",
                Duration.ofMinutes(1),
            )
        )

        var productPriceKey: ProductPriceKey? = null
        val productPrices = mutableListOf<ProductPrice>()
        val watcher = launch {
            while (isActive) {
                val (key, value) = channel.receive()
                productPriceKey = key
                productPrices.add(value)
                if (productPrices.size == 2) {
                    pollingJob.cancel()
                    cancel()
                }
            }
        }
        watcher.join()

        assertEquals(
            ProductPriceKey(
                Market.E_BEST,
                ProductType.SPOT,
                "078020",
                Duration.ofMinutes(1),
            ),
            productPriceKey
        )
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    ZonedDateTime.parse("2024-05-09T15:19+09"),
                    openPrice = 4925.0.toBigDecimal(),
                    highPrice = 4925.0.toBigDecimal(),
                    lowPrice = 4925.0.toBigDecimal(),
                    closePrice = 4925.0.toBigDecimal(),
                    volume = 3.0.toBigDecimal()
                ),
                Candle.TimeFrame.M1(
                    ZonedDateTime.parse("2024-05-09T15:29+09"),
                    openPrice = 4870.0.toBigDecimal(),
                    highPrice = 4870.0.toBigDecimal(),
                    lowPrice = 4870.0.toBigDecimal(),
                    closePrice = 4870.0.toBigDecimal(),
                    volume = 1152.0.toBigDecimal()
                )
            ), productPrices
        )
    }
}