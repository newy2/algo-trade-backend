package com.newy.algotrade.common.web.by_bit

import com.newy.algotrade.common.web.socket.WebSocketPing

class ByBitWebSocketPing : WebSocketPing(20 * 1000, """{"op":"ping"}""")