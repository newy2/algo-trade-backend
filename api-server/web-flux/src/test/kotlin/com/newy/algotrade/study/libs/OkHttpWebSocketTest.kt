package com.newy.algotrade.study.libs

import helpers.TestServerPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

open class BaseWebSocketListener(
    private val coroutineContext: CoroutineContext
) : WebSocketListener() {
    private val isOpened = Channel<Boolean>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        CoroutineScope(coroutineContext).launch {
            isOpened.send(true)
        }
    }

    suspend fun awaitOpen(): Boolean {
        return isOpened.receive()
    }
}

open class BaseTest {
    protected lateinit var server: MockWebServer
    private lateinit var request: Request

    @BeforeEach
    fun setUp() {
        val port = TestServerPort.nextValue()
        server = MockWebServer().also {
            it.start(port)
        }
        request = Request.Builder()
            .get()
            .url(server.url("/"))
            .build()
    }

    protected suspend fun initServerAndClient(
        serverListener: BaseWebSocketListener,
        clientListener: BaseWebSocketListener
    ): WebSocket {
        server.enqueue(MockResponse().withWebSocketUpgrade(serverListener))
        val client = OkHttpClient().newWebSocket(request, clientListener)

        serverListener.awaitOpen()
        clientListener.awaitOpen()

        return client
    }
}

class OkHttpWebSocketCancelTest : BaseTest() {
    class ServerListener(coroutineContext: CoroutineContext) : BaseWebSocketListener(coroutineContext) {
        lateinit var clientSocket: WebSocket
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            clientSocket = webSocket
        }
    }

    class ClientListener(coroutineContext: CoroutineContext) : BaseWebSocketListener(coroutineContext) {
        var log = ""
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            log += "onClosing "
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            log += "onClosed "
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            log += "onFailure "
        }
    }

    @Test
    fun `서버가 종료된 경우`() = runTest {
        val serverListener = ServerListener(coroutineContext)
        val clientListener = ClientListener(coroutineContext)
        initServerAndClient(serverListener, clientListener)

        server.close()
        delay(9)

        assertEquals("onFailure ", clientListener.log)
    }

    @ParameterizedTest
    @ValueSource(ints = [1000, 1001, 1002])
    fun `서버에서 커낵션을 종료한 경우`(closeCode: Int) = runBlocking {
        val serverListener = ServerListener(coroutineContext)
        val clientListener = ClientListener(coroutineContext)
        initServerAndClient(serverListener, clientListener)

        serverListener.clientSocket.close(closeCode, "")
        delay(9)

        assertEquals("onClosing ", clientListener.log)
    }

    @ParameterizedTest
    @ValueSource(ints = [1000, 1001, 1002])
    fun `클라이언트가 커낵션을 종료한 경우`(closeCode: Int) = runTest {
        val serverListener = ServerListener(coroutineContext)
        val clientListener = ClientListener(coroutineContext)
        val client = initServerAndClient(serverListener, clientListener)

        client.close(closeCode, "")
        delay(9)

        assertEquals("", clientListener.log)
    }
}

class OkHttpWebSocketSendMessageTest : BaseTest() {
    class ServerListener(coroutineContext: CoroutineContext) : BaseWebSocketListener(coroutineContext) {
        val set = mutableSetOf<String>()
        var log = ""

        override fun onMessage(webSocket: WebSocket, text: String) {
            log += "$text "
            set.add(text)
        }
    }

    @Test
    fun `메세지 전송`() = runBlocking {
        val serverListener = ServerListener(coroutineContext)
        val clientListener = BaseWebSocketListener(coroutineContext)
        val client = initServerAndClient(serverListener, clientListener)

        client.send("a")
        client.send("b")
        client.send("c")
        delay(9)

        assertEquals("a b c ", serverListener.log)
    }
}