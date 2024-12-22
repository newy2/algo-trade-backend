package com.newy.algotrade.common.web.by_bit

import com.newy.algotrade.common.coroutine.Polling
import com.newy.algotrade.common.coroutine.PollingCallback
import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.web.socket.WebSocketClient
import com.newy.algotrade.common.web.socket.WebSocketClientListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class ByBitWebSocket<K, V>(
    private val client: WebSocketClient,
    protected val jsonConverter: JsonConverter,
    private val coroutineContext: CoroutineContext,
    private val pollingCallback: PollingCallback<K, V>,
) : Polling<K> {
    private val subscribes = mutableSetOf<K>()

    init {
        client.setListener(ByBitWebSocketClientListener())
        client.setPingInfo(ByBitWebSocketPing())
    }

    abstract suspend fun parsingJson(message: String): Pair<K, V>?
    abstract fun topic(key: K): String

    override suspend fun start() {
        client.start()
    }

    override fun subscribe(key: K) {
        subscribes.add(key)
        client.send(
            jsonConverter.toJson(
                mapOf(
                    "op" to "subscribe",
                    "args" to arrayOf(topic(key))
                )
            )
        )
    }

    override fun unSubscribe(key: K) {
        subscribes.remove(key)
        client.send(
            jsonConverter.toJson(
                mapOf(
                    "op" to "unsubscribe",
                    "args" to arrayOf(topic(key))
                )
            )
        )
    }

    override fun cancel() {
        client.cancel()
    }

    private fun sendSubscribeMessage() {
        if (subscribes.isEmpty()) {
            return
        }

        client.send(
            jsonConverter.toJson(
                mapOf(
                    "op" to "subscribe",
                    "args" to subscribes.map { topic(it) }
                )
            )
        )
    }

    private inner class ByBitWebSocketClientListener : WebSocketClientListener() {
        override fun onOpen() {
            sendSubscribeMessage()
        }

        override fun onRestart() {
            sendSubscribeMessage()
        }

        override fun onMessage(message: String) {
            CoroutineScope(coroutineContext).launch {
                parsingJson(message)?.let { (key, value) ->
                    pollingCallback(Pair(key, value))
                }
            }
        }
    }
}
