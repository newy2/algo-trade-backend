package com.newy.algotrade.coroutine_based_application.common.web.by_bit

import com.newy.algotrade.coroutine_based_application.common.web.WebSocketPing

class ByBitWebSocketPing : WebSocketPing(20 * 1000, """{"op":"ping"}""")