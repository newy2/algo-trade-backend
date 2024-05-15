package com.newy.algotrade.unit.common.web

import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClientListener
import com.newy.algotrade.coroutine_based_application.common.web.socket.DefaultWebSocketClient
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketPing
import helpers.TestServerPort
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

class PingTest {
    private val port = TestServerPort.nextValue()
    private lateinit var serverListener: ServerListener
    private lateinit var server: MockWebServer
    private lateinit var client: DefaultWebSocketClient

    @BeforeEach
    fun setUp() {
        serverListener = ServerListener()
        server = MockWebServer().also {
            it.start(port)
            repeat(1000) { _ ->
                it.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
            }
        }
    }

    class ServerListener : WebSocketListener() {
        var pingCount = 0

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (text == "ping") {
                pingCount++
            }
        }
    }

    @Test
    fun `ping 메세지 전송 확인`() = runBlocking {
        client = object : DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext,
            WebSocketPing(10, "ping"),
        ) {}

        client.start()

        delay(25)
        client.cancel()

        assertEquals(2, serverListener.pingCount)
    }

    @Test
    fun `restart 하는 경우, ping interval 이 초기화 됨`() = runBlocking {
        client = object : DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext,
            WebSocketPing(10, "ping"),
        ) {}

        client.start()
        delay(9)
        client.restart()

        delay(15)
        client.cancel()

        assertEquals(1, serverListener.pingCount)
    }
}

class AutoRestartTest {
    private val port = TestServerPort.nextValue()
    private lateinit var serverListener: ServerListener
    private lateinit var server: MockWebServer
    private lateinit var client: DefaultWebSocketClient

    @BeforeEach
    fun setUp() {
        serverListener = ServerListener()
        server = MockWebServer().also {
            it.start(port)
            repeat(1000) { _ ->
                it.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
            }
        }
    }

    class ServerListener : WebSocketListener() {
        private var clientSocket: WebSocket? = null

        override fun onOpen(webSocket: WebSocket, response: Response) {
            clientSocket = webSocket
        }

        fun disconnectClientSocket() {
            clientSocket?.close(1001, "")
            clientSocket = null
        }
    }

    @Disabled
    @Test
    @Timeout(6)
    fun `서버 에러시, 자동 재시작`() = runBlocking {
        client = object : DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext,
        ) {}

        var openCount = 0
        var restartCount = 0
        client.setListener(object : WebSocketClientListener() {
            override fun onOpen() {
                openCount++
            }

            override fun onRestart() {
                restartCount++
            }
        })

        client.start()

        serverListener.disconnectClientSocket()

        delay(5000)
        client.cancel()

        assertEquals(2, openCount)
        assertEquals(1, restartCount)
    }
}

class SendReceiveMessageTest {
    private val port = TestServerPort.nextValue()
    private lateinit var server: MockWebServer
    private lateinit var client: DefaultWebSocketClient

    @BeforeEach
    fun setUp() {
        server = MockWebServer().also {
            it.start(port)
        }
    }

    class ServerListener(private val coroutineContext: CoroutineContext) : WebSocketListener() {
        private lateinit var clientSocket: WebSocket
        var log = ""
        override fun onOpen(webSocket: WebSocket, response: Response) {
            clientSocket = webSocket
            CoroutineScope(coroutineContext).launch {
                while (isActive) {
                    delay(10)
                    webSocket.send("""{"id":"a","value":1}""")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log += "$text "
        }
    }

    @Test
    fun `메세지 수신`() = runBlocking {
        val serverListener = ServerListener(coroutineContext)
        repeat(1000) { _ ->
            server.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
        }

        val results = mutableListOf<String>()
        client = object : DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext,
            listener = object : WebSocketClientListener() {
                override fun onMessage(message: String) {
                    results.add(message)
                }
            }
        ) {}

        client.start()

        delay(28)
        client.cancel()

        assertEquals("""{"id":"a","value":1}""", results[0])
        assertEquals(2, results.size)
    }

    @Test
    fun `메세지 발신`() = runBlocking {
        val serverListener = ServerListener(coroutineContext)
        server.enqueue(MockResponse().withWebSocketUpgrade(serverListener))

        client = object : DefaultWebSocketClient(
            OkHttpClient(),
            "http://localhost:$port",
            coroutineContext = coroutineContext,
        ) {}

        client.start()

        client.send("a")
        client.send("b")
        client.send("c")
        delay(10)
        client.cancel()

        assertEquals("a b c ", serverListener.log)
    }
}