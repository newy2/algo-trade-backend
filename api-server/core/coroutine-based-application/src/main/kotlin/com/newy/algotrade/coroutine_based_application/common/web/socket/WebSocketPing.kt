package com.newy.algotrade.coroutine_based_application.common.web.socket

open class WebSocketPing(val intervalMillis: Long, val message: String)

class ByBitWebSocketPing : WebSocketPing(20 * 1000, """{"op":"ping"}""")