package com.newy.algotrade.coroutine_based_application.common.web.by_bit

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClient
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClientListener
import com.newy.algotrade.domain.common.mapper.JsonConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class ByBitWebSocket<K, V>(
    private val client: WebSocketClient,
    protected val jsonConverter: JsonConverter,
    private val coroutineContext: CoroutineContext,
    override var callback: PollingCallback<K, V>? = null,
) : Polling<K, V>, WebSocketClientListener() {
    private val subscribes = mutableSetOf<K>()

    init {
        client.setListener(this)
        client.setPingInfo(ByBitWebSocketPing())
    }

    abstract suspend fun parsingJson(message: String): Pair<K, V>?
    abstract fun topic(key: K): String

    override fun onOpen() {
        sendSubscribeMessage()
    }

    override fun onRestart() {
        sendSubscribeMessage()
    }

    override fun onMessage(json: String) {
        CoroutineScope(coroutineContext).launch {
            parsingJson(json)?.let { (key, value) ->
                onNextTick(key, value)
            }
        }
    }

    override suspend fun start() {
        client.start()
    }

    override suspend fun subscribe(key: K) {
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
}
