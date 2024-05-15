package com.newy.algotrade.coroutine_based_application.common.coroutine

typealias PollingCallback<K, V> = suspend (Pair<K, V>) -> Unit

@Suppress("INAPPLICABLE_JVM_NAME")
interface Polling<K, V> {
    var callback: PollingCallback<K, V>?
    suspend fun start()
    fun cancel()
    suspend fun subscribe(key: K)
    fun unSubscribe(key: K)
    suspend fun onNextTick(key: K, value: V) {
        callback?.invoke(Pair(key, value))
    }

    @JvmName("_setCallback")
    fun setCallback(callback: PollingCallback<K, V>) {
        this.callback = callback
    }
}