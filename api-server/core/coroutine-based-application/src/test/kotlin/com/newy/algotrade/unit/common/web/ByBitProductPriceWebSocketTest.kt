package com.newy.algotrade.unit.common.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.web.by_bit.ByBitWebSocketPing
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.web.PollingProductPriceWithByBitWebSocket
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestServerPort
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

open class BaseByBitProductPriceWebSocketTest {
    protected val port = TestServerPort.nextValue()

    protected fun newClient(
        coroutineContext: CoroutineContext,
        callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit = {}
    ) =
        PollingProductPriceWithByBitWebSocket(
            DefaultWebSocketClient(
                OkHttpClient(),
                "http://localhost:$port",
                coroutineContext,
            ),
            ProductType.SPOT,
            JsonConverterByJackson(jacksonObjectMapper()),
            coroutineContext,
        ).also {
            it.setCallback(callback)
        }

}

class ByBitProductPriceWebSocketSendMessageTest : BaseByBitProductPriceWebSocketTest() {
    private lateinit var serverListener: ServerListener
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        serverListener = ServerListener()
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
        }
    }

    class ServerListener : WebSocketListener() {
        val receiveMessage = Channel<String>()

        override fun onMessage(webSocket: WebSocket, text: String) {
            CoroutineScope(Dispatchers.Default).launch {
                receiveMessage.send(text)
            }
        }
    }

    @Test
    fun `핑 메세지`() = runTest {
        val socket = newClient(coroutineContext)

        socket.start()
        delay(20 * 1000)

        val pingMessage = serverListener.receiveMessage.receive()
        socket.cancel()

        assertEquals("""{"op":"ping"}""", pingMessage)
        assertEquals(20 * 1000, ByBitWebSocketPing().intervalMillis)
        assertEquals("""{"op":"ping"}""", ByBitWebSocketPing().message)
    }

    @Test
    fun `subscribe 메세지`() = runBlocking {
        val client = newClient(coroutineContext)

        client.start()
        client.subscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            )
        )

        val message = serverListener.receiveMessage.receive()
        client.cancel()

        assertEquals("""{"op":"subscribe","args":["kline.1.BTCUSDT"]}""", message)
    }

    @Test
    fun `unsubscribe 메세지`() = runBlocking {
        val client = newClient(coroutineContext)

        client.start()
        client.unSubscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            )
        )

        val message = serverListener.receiveMessage.receive()
        client.cancel()

        assertEquals("""{"op":"unsubscribe","args":["kline.1.BTCUSDT"]}""", message)
    }

    @Test
    fun `시작하기 전에 subscribe 한 경우 - 접속 후 subscribe 메세지 전송`() = runBlocking {
        val client = newClient(coroutineContext)

        client.subscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            )
        )
        client.subscribe(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(5)
            )
        )
        client.start()


        val message = serverListener.receiveMessage.receive()
        client.cancel()

        assertEquals("""{"op":"subscribe","args":["kline.1.BTCUSDT","kline.5.BTCUSDT"]}""", message)
    }
}

class ByBitProductPriceWebSocketReceiveMessageTest : BaseByBitProductPriceWebSocketTest() {
    private lateinit var serverListener: ServerListener
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        serverListener = ServerListener()
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
        }
    }

    class ServerListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send(
                """
                    {
                        "type": "snapshot",
                        "topic": "kline.1.BTCUSDT",
                        "data": [
                            {
                                "start": 1715609400000,
                                "end": 1715609459999,
                                "interval": "1",
                                "open": "55757.8",
                                "close": "55757.77",
                                "high": "55757.8",
                                "low": "55757.77",
                                "volume": "0.0001",
                                "turnover": "5.575777",
                                "confirm": false,
                                "timestamp": 1715609427781
                            }
                        ],
                        "ts": 1715609427781
                    }
                """.trimIndent()
            )
        }
    }

    @Test
    fun `메세지 파싱 - 결과값이 1개인 경우`() = runBlocking {
        val receiveMessage = Channel<Pair<ProductPriceKey, List<ProductPrice>>>()
        val client = newClient(coroutineContext) {
            receiveMessage.send(it)
        }

        client.start()

        val (productPriceKey, productPrices) = receiveMessage.receive()
        client.cancel()

        assertEquals(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            productPriceKey
        )
        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    beginTime = 1715609400000,
                    openPrice = "55757.8".toBigDecimal(),
                    closePrice = "55757.77".toBigDecimal(),
                    highPrice = "55757.8".toBigDecimal(),
                    lowPrice = "55757.77".toBigDecimal(),
                    volume = "0.00010".toBigDecimal(),
                )
            ),
            productPrices
        )
    }
}

class ByBitProductPriceWebSocketReceiveMessageTest2 : BaseByBitProductPriceWebSocketTest() {
    private lateinit var serverListener: ServerListener
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        serverListener = ServerListener()
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
        }
    }

    class ServerListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send(
                """
                    {
                        "success": true,
                        "ret_msg": "subscribe",
                        "conn_id": "1c01c33d-b7ad-4ee3-bebb-2403a320d289",
                        "op": "subscribe"
                    }
                """.trimIndent()
            )
        }
    }

    @Test
    fun `접속 완료 데이터인 경우 무시한다`() = runTest {
        val receiveMessage = Channel<Pair<ProductPriceKey, List<ProductPrice>>>()
        val client = newClient(coroutineContext) {
            receiveMessage.send(it)
        }

        client.start()
        delay(1000)

        val results = receiveMessage.tryReceive()
        client.cancel()

        assertNull(results.getOrNull())
    }
}