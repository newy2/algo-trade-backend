package com.newy.algotrade.coroutine_based_application.common.coroutine

interface Polling<T> {
    suspend fun start()
    fun cancel()
    suspend fun subscribe(data: T)
    fun unSubscribe(data: T)
}