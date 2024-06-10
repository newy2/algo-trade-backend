package com.newy.algotrade.integration.product.adapter.out.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.EBestAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.*
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


private fun newClient(
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit = {}
): Polling<ProductPriceKey, List<ProductPrice>> {
    val loadProductPriceProxy = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.EBest.url,
        JsonConverterByJackson(jacksonObjectMapper())
    ).let {
        FetchProductPriceProxy(
            mapOf(
                Market.E_BEST to FetchEBestProductPrice(
                    it,
                    EBestAccessTokenHttpApi(it),
                    PrivateApiInfo(
                        key = TestEnv.EBest.apiKey,
                        secret = TestEnv.EBest.apiSecret,
                    )
                )
            )
        )
    }

    return PollingProductPriceProxy(
        mapOf(
            PollingProductPriceProxy.Key(Market.E_BEST, ProductType.SPOT) to PollingProductPriceWithHttpApi(
                loadProductPriceProxy,
                1000,
                coroutineContext,
            ),
            PollingProductPriceProxy.Key(Market.BY_BIT, ProductType.SPOT) to PollingProductPriceWithByBitWebSocket(
                DefaultWebSocketClient(
                    OkHttpClient(),
                    TestEnv.ByBit.socketUrl,
                    coroutineContext,
                ),
                ProductType.SPOT,
                JsonConverterByJackson(jacksonObjectMapper()),
                coroutineContext,
            )
        ),
        object : OnReceivePollingPricePort {
            override suspend fun onReceivePrice(
                productPriceKey: ProductPriceKey,
                productPriceList: List<ProductPrice>
            ) {
                CoroutineScope(coroutineContext).launch {
                    callback(productPriceKey to productPriceList)
                }
            }
        }
    )
}


class ByBitWebSocketTest {
    @Test
    fun `바이빗 웹소켓 response 테스트`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, List<ProductPrice>>>()

        val client = newClient(coroutineContext) {
            channel.send(it)
        }

        client.subscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            )
        )
        client.start()

        val response = channel.receive()
        client.cancel()

        assertEquals(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            response.first
        )

        assertNotNull(response.second[0])
        response.second[0].let {
            val now = OffsetDateTime.now()
                .plusMinutes(if (response.second.size == 2) 0 else 1)
                .withSecond(0)
                .withNano(0)
            val endTime = it.time.end

            assertTrue(now.isEqual(endTime))
            it.price.let { price ->
                /***
                 * TODO refector this
                 * 딱히 현재 시간의 금액을 확인할 방법이 떠오르지 않음
                 *
                 * 방법1.
                 * 바이빗 웹소켓에서 가격정보 array.size 가 2로 내려오는 경우, 첫번째 element 가 확정된 가격임
                 * 하지만, array.size 2개 짜리가 내려올 때까지 기다리는 건 비효율 적임
                 */

                assertTrue(price.open > 0.toBigDecimal())
                assertTrue(price.high > 0.toBigDecimal())
                assertTrue(price.close > 0.toBigDecimal())
                assertTrue(price.low > 0.toBigDecimal())
            }
        }
    }
}

class EBestHttpPollingTest {
    @Test
    fun `이베스트 Http 폴링 response 테스트`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, List<ProductPrice>>>()

        val client = newClient(coroutineContext) {
            channel.send(it)
        }

        client.subscribe(
            ProductPriceKey(
                Market.E_BEST,
                ProductType.SPOT,
                "078020",
                Duration.ofMinutes(1),
            )
        )
        client.start()

        val response = channel.receive()
        client.cancel()

        assertEquals(
            ProductPriceKey(
                Market.E_BEST,
                ProductType.SPOT,
                "078020",
                Duration.ofMinutes(1),
            ),
            response.first
        )

        /***
         * TODO refector this
         * 이건 바이빗 보다 더 답이 없음. 장마감 시간 고려 해야함
         */
        assertEquals(2, response.second.size)
    }
}