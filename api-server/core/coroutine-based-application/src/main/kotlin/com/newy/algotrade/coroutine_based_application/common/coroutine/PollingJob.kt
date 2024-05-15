package com.newy.algotrade.coroutine_based_application.common.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext

abstract class PollingJob<K, V>(
    private val delayMillis: Long,
    private val coroutineContext: CoroutineContext,
    override var callback: PollingCallback<K, V>? = null
) : Polling<K, V> {
    private lateinit var intervalTick: ReceiveChannel<Unit>
    private val channel = Channel<K>()
    private val producers: MutableMap<K, Job> = mutableMapOf()

    abstract suspend fun eachProcess(key: K): V

    override suspend fun start() {
        intervalTick = ticker(delayMillis, initialDelayMillis = 0, coroutineContext)
        CoroutineScope(coroutineContext).launch {
            for (nextTick in intervalTick) {
                val key = channel.receive()
                val value = eachProcess(key)
                onNextTick(key, value)
            }
        }
    }

    override fun cancel() {
        intervalTick.cancel()
        coroutineContext.cancelChildren()
    }

    override suspend fun subscribe(key: K) {
        producers.put(key, CoroutineScope(coroutineContext).launch {
            while (isActive) {
                channel.send(key)
                yield()
            }
        })?.cancel()
    }

    override fun unSubscribe(key: K) {
        producers.getValue(key).cancel()
    }
}