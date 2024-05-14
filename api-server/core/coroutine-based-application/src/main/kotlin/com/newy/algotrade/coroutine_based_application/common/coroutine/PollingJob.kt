package com.newy.algotrade.coroutine_based_application.common.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext

abstract class PollingJob<T, R>(
    private val delayMillis: Long,
    private val coroutineContext: CoroutineContext,
    private val callback: suspend (Pair<T, R>) -> Unit
) : Polling<T> {
    private lateinit var intervalTick: ReceiveChannel<Unit>
    private val channel = Channel<T>()
    private val producers: MutableMap<T, Job> = mutableMapOf()

    abstract suspend fun eachProcess(data: T): R

    override suspend fun start() {
        intervalTick = ticker(delayMillis, initialDelayMillis = 0, coroutineContext)
        CoroutineScope(coroutineContext).launch {
            for (nextTick in intervalTick) {
                val key = channel.receive()
                val value = eachProcess(key)
                callback(key to value)
            }
        }
    }

    override fun cancel() {
        intervalTick.cancel()
        coroutineContext.cancelChildren()
    }

    override suspend fun subscribe(data: T) {
        producers.put(data, CoroutineScope(coroutineContext).launch {
            while (isActive) {
                channel.send(data)
                yield()
            }
        })?.cancel()
    }

    override fun unSubscribe(data: T) {
        producers.getValue(data).cancel()
    }
}