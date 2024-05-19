package com.newy.algotrade.integration.price.adapter.out.web.worker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.web.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.ByBitLoadProductPriceHttpApi
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.LoadProductPriceProxy
import com.newy.algotrade.coroutine_based_application.price.adpter.out.web.worker.PollingLoadProductPrice
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
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
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

class PollingLoadProductPriceTestHelper(
    loader: LoadProductPricePort,
    delayMillis: Long,
    coroutineContext: CoroutineContext,
    callback: PollingCallback<ProductPriceKey, List<ProductPrice>>
) : PollingLoadProductPrice(loader, delayMillis, coroutineContext) {
    init {
        setCallback(callback)
    }

    override fun endTime(): OffsetDateTime {
        return OffsetDateTime.parse("2024-05-09T00:00+09:00")
    }

    override fun limit(): Int {
        return 2
    }
}

class PollingByBitLoadProductPriceTest {
    private val client = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.ByBit.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    private val api = LoadProductPriceProxy(
        mapOf(
            Market.BY_BIT to ByBitLoadProductPriceHttpApi(client)
        )
    )

    @Test
    fun `바이빗 가격정보 폴링`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, ProductPrice>>()
        var index = 0

        val pollingJob = PollingLoadProductPriceTestHelper(api, delayMillis = 1000, coroutineContext) { (key, list) ->
            channel.send(Pair(key, list[index++])) // 실시간 API 흉내를 내기 위해서, index 사용
        }

        pollingJob.start()
        pollingJob.subscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
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
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            productPriceKey
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
            ), productPrices
        )
    }
}