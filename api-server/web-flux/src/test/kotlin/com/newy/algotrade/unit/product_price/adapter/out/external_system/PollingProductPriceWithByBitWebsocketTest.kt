package com.newy.algotrade.unit.product_price.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import com.newy.algotrade.common.extension.ProductPrice
import com.newy.algotrade.common.web.by_bit.ByBitWebSocketPing
import com.newy.algotrade.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.product_price.adapter.out.external_system.PollingProductPriceWithByBitWebSocket
import com.newy.algotrade.product_price.domain.ProductPriceKey
import helpers.TestServerPort
import helpers.productPriceKey
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
import org.junit.jupiter.api.DisplayName
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
    ) = PollingProductPriceWithByBitWebSocket(
        productType = ProductType.SPOT,
        client = DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext,
        ),
        jsonConverter = JsonConverterByJackson(jacksonObjectMapper()),
        coroutineContext = coroutineContext,
        pollingCallback = callback,
    )
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
    fun `ByBit 웹소켓은 20초 주기로 핑 메세지를 전송한다`() = runTest {
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
    fun `subscribe 메세지 전송하기`() = runBlocking {
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
    fun `unsubscribe 메세지 전송하기`() = runBlocking {
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
    fun `start 하기 전에 subscribe 한 경우 - 웹소켓 접속 완료 후 subscribe 메세지가 전송된다`() = runBlocking {
        val client = newClient(coroutineContext).also {
            it.subscribe(
                productPriceKey(
                    productCode = "BTCUSDT",
                    interval = Duration.ofMinutes(1)
                )
            )
            it.subscribe(
                productPriceKey(
                    productCode = "BTCUSDT",
                    interval = Duration.ofMinutes(5)
                )
            )
        }

        client.start()

        val message = serverListener.receiveMessage.receive()
        client.cancel()

        assertEquals("""{"op":"subscribe","args":["kline.1.BTCUSDT","kline.5.BTCUSDT"]}""", message)
    }
}

@DisplayName("가격 정보 메세지 수신 테스트")
class ByBitProductPriceWebSocketReceiveMessageTest : BaseByBitProductPriceWebSocketTest() {
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().withWebSocketUpgrade(ServerListener()))
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

        val (receiveProductPriceKey, receiveProductPrices) = receiveMessage.receive()
        client.cancel()

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
                    beginTime = 1715609400000,
                    openPrice = "55757.8".toBigDecimal(),
                    closePrice = "55757.77".toBigDecimal(),
                    highPrice = "55757.8".toBigDecimal(),
                    lowPrice = "55757.77".toBigDecimal(),
                    volume = "0.00010".toBigDecimal(),
                )
            ),
            receiveProductPrices
        )
    }
}

@DisplayName("가격 정보 이외의 메세지 수신 테스트")
class ByBitProductPriceWebSocketReceiveMessageTest2 : BaseByBitProductPriceWebSocketTest() {
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().withWebSocketUpgrade(ServerListener()))
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