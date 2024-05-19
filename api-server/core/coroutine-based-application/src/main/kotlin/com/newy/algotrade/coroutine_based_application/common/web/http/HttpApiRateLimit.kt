package com.newy.algotrade.coroutine_based_application.common.web.http

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class HttpApiRateLimit(
    val delayMillis: Long,
    private val context: CoroutineContext = EmptyCoroutineContext,
    private val tickers: MutableMap<String, ReceiveChannel<Unit>> = mutableMapOf()
) {
    companion object {
        const val DEFAULT_REQUEST_ID = "localhost"
    }

    fun cancel(cause: CancellationException? = null) {
        tickers.values.forEach { it.cancel(cause) }
    }

    suspend fun await(requestId: String = DEFAULT_REQUEST_ID) {
        ticker(requestId).receive()
    }

    private fun ticker(requestId: String) =
        tickers[requestId] ?: newTicker().also {
            tickers[requestId] = it
        }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun newTicker() =
        ticker(delayMillis, initialDelayMillis = 0, context)
}