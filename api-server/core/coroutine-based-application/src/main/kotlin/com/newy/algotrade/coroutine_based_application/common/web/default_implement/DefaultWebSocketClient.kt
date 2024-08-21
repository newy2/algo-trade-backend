package com.newy.algotrade.coroutine_based_application.common.web.default_implement

import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClient
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClientListener
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketPing
import com.newy.algotrade.domain.common.annotation.ForTesting
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import okhttp3.*
import kotlin.coroutines.CoroutineContext

open class DefaultWebSocketClient(
    val client: OkHttpClient,
    private val url: String,
    @ForTesting private val coroutineContext: CoroutineContext = Dispatchers.IO,
    pingInfo: WebSocketPing = WebSocketPing(20 * 1000, ""),
    listener: WebSocketClientListener = WebSocketClientListener(),
) : WebSocketClient(pingInfo, listener) {
    private lateinit var pingTicker: ReceiveChannel<Unit>
    private var isOpened = false
    private val awaitOpened: Channel<Unit> = Channel()
    private lateinit var socket: WebSocket

    private var isRequestClose = false

    override suspend fun start() {
        socket = client.newWebSocket(Request.Builder().get().url(url).build(), Listener())
        awaitOpened.receive()
        setPingTicker()
    }

    override fun send(message: String) {
        if (!isOpened) {
            return
        }
        socket.send(message)
    }

    override fun cancel() {
        isRequestClose = true
        socket.cancel()
        pingTicker.cancel()
        coroutineContext.cancelChildren()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun setPingTicker() {
        pingTicker = ticker(pingInfo.intervalMillis, context = coroutineContext)
        CoroutineScope(coroutineContext).launch {
            for (nextTick in pingTicker) {
                socket.send(pingInfo.message)
            }
        }
    }

    private inner class Listener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            CoroutineScope(coroutineContext).launch {
                isOpened = true
                awaitOpened.send(Unit)
                listener.onOpen()
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            if (!isRequestClose) {
                CoroutineScope(coroutineContext).launch {
                    this@DefaultWebSocketClient.restart()
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (!isRequestClose) {
                CoroutineScope(coroutineContext).launch {
                    this@DefaultWebSocketClient.restart()
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            CoroutineScope(coroutineContext).launch {
                listener.onMessage(text)
            }
        }
    }
}