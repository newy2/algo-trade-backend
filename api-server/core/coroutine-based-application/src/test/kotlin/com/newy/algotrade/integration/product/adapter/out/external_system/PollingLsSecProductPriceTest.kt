package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.FetchProductPriceProxy
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
import kotlin.test.assertEquals

class PollingLsSecProductPriceTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.LsSec.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val accessTokenLoader = LsSecAccessTokenHttpApi(client)
    private val api = FetchProductPriceProxy(
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

    @Test
    fun `LS증권 가격정보 폴링`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, ProductPrice>>()
        var index = 0

        val pollingJob = PollingProductPriceTestHelper(api, delayMillis = 1000, coroutineContext) { (key, list) ->
            channel.send(Pair(key, list[index++])) // 실시간 API 흉내를 내기 위해서, index 사용
        }

        pollingJob.start()
        pollingJob.subscribe(
            ProductPriceKey(
                Market.LS_SEC,
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
                Market.LS_SEC,
                ProductType.SPOT,
                "078020",
                Duration.ofMinutes(1),
            ),
            productPriceKey
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
            ), productPrices
        )
    }
}