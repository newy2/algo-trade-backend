package com.newy.algotrade.coroutine_based_application.common.coroutine

typealias PollingCallback<K, V> = suspend (Pair<K, V>) -> Unit

@Suppress("INAPPLICABLE_JVM_NAME")
interface Polling<K, V> {
    suspend fun start()
    fun cancel()
    fun subscribe(key: K)
    fun unSubscribe(key: K)
}