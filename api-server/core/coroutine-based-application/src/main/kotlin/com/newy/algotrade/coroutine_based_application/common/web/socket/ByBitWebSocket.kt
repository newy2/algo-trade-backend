package com.newy.algotrade.coroutine_based_application.common.web.socket

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClient
import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClientListener
import com.newy.algotrade.domain.common.mapper.JsonConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class ByBitWebSocket<T, R>(
    private val client: WebSocketClient,
    protected val jsonConverter: JsonConverter,
    private val coroutineContext: CoroutineContext,
    private val callback: suspend (R) -> Unit,
) : Polling<T>, WebSocketClientListener() {
    private val subscribes = mutableSetOf<T>()

    init {
        client.setListener(this)
    }

    abstract suspend fun eachProcess(message: String): R?
    abstract fun parsing(data: T): String

    override fun onOpen() {
        sendSubscribeMessage()
    }

    override fun onRestart() {
        sendSubscribeMessage()
    }

    override fun onMessage(message: String) {
        CoroutineScope(coroutineContext).launch {
            eachProcess(message)?.let {
                callback(it)
            }
        }
    }

    override suspend fun start() {
        client.start()
    }

    override suspend fun subscribe(data: T) {
        subscribes.add(data)
        client.send(
            jsonConverter.toJson(
                mapOf(
                    "op" to "subscribe",
                    "args" to arrayOf(
                        parsing(data)
                    )
                )
            )
        )
    }

    override fun unSubscribe(data: T) {
        subscribes.remove(data)
        client.send(
            jsonConverter.toJson(
                mapOf(
                    "op" to "unsubscribe",
                    "args" to arrayOf(
                        parsing(data)
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
