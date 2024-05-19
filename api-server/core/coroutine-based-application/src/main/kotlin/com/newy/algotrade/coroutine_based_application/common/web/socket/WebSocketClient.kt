package com.newy.algotrade.coroutine_based_application.common.web.socket

abstract class WebSocketClient(
    protected var pingInfo: WebSocketPing,
    protected var listener: WebSocketClientListener,
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

    @JvmName("_setPingInfo")
    fun setPingInfo(pingInfo: WebSocketPing) {
        this.pingInfo = pingInfo
    }

    abstract fun send(message: String)
}

open class WebSocketClientListener {
    open fun onOpen() {}
    open fun onMessage(message: String) {}
    open fun onRestart() {}
}