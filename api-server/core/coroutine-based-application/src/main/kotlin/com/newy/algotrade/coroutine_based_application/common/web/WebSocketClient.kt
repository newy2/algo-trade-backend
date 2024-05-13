package com.newy.algotrade.coroutine_based_application.common.web

abstract class WebSocketClient(
    protected var listener: WebSocketClientListener
) {

    abstract suspend fun start()

    abstract fun cancel()

    suspend fun restart() {
        cancel()
        start()
        listener.onRestart()
    }

    @JvmName("_setListener")
    fun setListener(listener: WebSocketClientListener) {
        this.listener = listener
    }

    abstract fun send(message: String)
}

open class WebSocketClientListener {
    open fun onOpen() {}
    open fun onMessage(message: String) {}
    open fun onRestart() {}
}