package com.newy.algotrade.coroutine_based_application.common.coroutine

interface Polling<K, V> {
    val callback: suspend (Pair<K, V>) -> Unit
    suspend fun start()
    fun cancel()
    suspend fun subscribe(key: K)
    fun unSubscribe(key: K)
    suspend fun onNextTick(key: K, value: V) {
        callback(Pair(key, value))
    }
}