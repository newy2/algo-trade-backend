package com.newy.algotrade.coroutine_based_application.common.web.socket

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClient
import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClientListener
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

    abstract suspend fun eachProcess(message: String): Pair<K, V>?
    abstract fun parsing(key: K): String

    override fun onOpen() {
        sendSubscribeMessage()
    }

    override fun onRestart() {
        sendSubscribeMessage()
    }

    override fun onMessage(message: String) {
        CoroutineScope(coroutineContext).launch {
            eachProcess(message)?.let { (key, value) ->
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
                    "args" to arrayOf(
                        parsing(key)
                    )
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
                    "args" to arrayOf(
                        parsing(key)
                    )
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
                    "args" to subscribes.map { parsing(it) }
                )
            )
        )
    }
}
