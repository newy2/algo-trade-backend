package com.newy.algotrade.integration.product.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.product_price.adapter.out.external_system.*
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.PollingProductPricePort
import helpers.BaseDisabledTest
import helpers.TestEnv
import helpers.productPriceKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


private fun newClient(
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit = {}
): PollingProductPricePort {
    val lsSecHttpApiClient = DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.LsSec.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )
    val byBitWebSocketClient = DefaultWebSocketClient(
        OkHttpClient(),
        TestEnv.ByBit.socketUrl,
        coroutineContext,
    )

    return PollingProductPriceProxyAdapter(
        mapOf(
            PollingProductPriceProxyAdapter.Key(
                Market.LS_SEC,
                ProductType.SPOT,
            ) to PollingProductPriceWithHttpClient(
                loader = FetchLsSecProductPrice(
                    client = lsSecHttpApiClient,
                    accessTokenLoader = com.newy.algotrade.auth.adpter.out.external_system.LsSecAccessTokenHttpApi(
                        lsSecHttpApiClient
                    ),
                    masterUserInfo = PrivateApiInfo(
                        key = TestEnv.LsSec.apiKey,
                        secret = TestEnv.LsSec.apiSecret,
                    ),
                ),
                delayMillis = 1000,
                coroutineContext = coroutineContext,
                pollingCallback = callback,
            ),
            PollingProductPriceProxyAdapter.Key(
                Market.BY_BIT,
                ProductType.SPOT,
            ) to PollingProductPriceWithByBitWebSocket(
                productType = ProductType.SPOT,
                client = byBitWebSocketClient,
                jsonConverter = JsonConverterByJackson(jacksonObjectMapper()),
                coroutineContext = coroutineContext,
                pollingCallback = callback,
            )
        )
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
            productPriceKey(
                productCode = "BTCUSDT",
                interval = Duration.ofMinutes(1)
            )
        )
        client.start()

        val response = channel.receive()
        client.cancel()

        assertEquals(
            productPriceKey(
                productCode = "BTCUSDT",
                interval = Duration.ofMinutes(1)
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

class LsSecHttpPollingTest : BaseDisabledTest {
    @DisabledIf("hasNotLsSecApiInfo")
    @Test
    fun `LS증권 Http 폴링 response 테스트`() = runBlocking {
        val channel = Channel<Pair<ProductPriceKey, List<ProductPrice>>>()

        val client = newClient(coroutineContext) {
            channel.send(it)
        }

        client.subscribe(
            productPriceKey(
                productCode = "078020",
                interval = Duration.ofMinutes(1),
            )
        )
        client.start()

        val response = channel.receive()
        client.cancel()

        assertEquals(
            productPriceKey(
                productCode = "078020",
                interval = Duration.ofMinutes(1),
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