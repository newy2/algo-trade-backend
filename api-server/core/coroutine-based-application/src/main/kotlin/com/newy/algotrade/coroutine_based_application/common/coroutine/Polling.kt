package com.newy.algotrade.coroutine_based_application.common.coroutine

typealias PollingCallback<K, V> = suspend (Pair<K, V>) -> Unit

interface Polling<K> {
    suspend fun start()
    fun cancel()
    fun subscribe(key: K)
    fun unSubscribe(key: K)
}