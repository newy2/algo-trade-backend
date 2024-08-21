package com.newy.algotrade.coroutine_based_application.common.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext

abstract class PollingJob<K, V>(
    private val delayMillis: Long,
    private val coroutineContext: CoroutineContext,
    private val pollingCallback: PollingCallback<K, V>
) : Polling<K, V> {
    private lateinit var nextTick: ReceiveChannel<Unit>
    private val nextJob = Channel<K>()
    private val jobRequesters: MutableMap<K, Job> = mutableMapOf()

    abstract suspend fun eachProcess(key: K): V

    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun start() {
        nextTick = ticker(delayMillis, initialDelayMillis = 0, coroutineContext)
        CoroutineScope(coroutineContext).launch {
            for (tick in nextTick) {
                val key = nextJob.receive()
                val value = eachProcess(key)
                pollingCallback(Pair(key, value))
            }
        }
    }

    override fun cancel() {
        nextTick.cancel()
        coroutineContext.cancelChildren()
    }

    override fun subscribe(key: K) {
        jobRequesters.put(key, CoroutineScope(coroutineContext).launch {
            while (isActive) {
                nextJob.send(key)
                yield()
            }
        })?.cancel()
    }

    override fun unSubscribe(key: K) {
        jobRequesters.getValue(key).cancel()
    }
}