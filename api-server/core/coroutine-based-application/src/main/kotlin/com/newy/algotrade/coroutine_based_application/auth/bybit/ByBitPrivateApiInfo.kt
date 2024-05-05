package com.newy.algotrade.coroutine_based_application.auth.bybit

import com.newy.algotrade.domain.auth.PrivateApiInfo

class ByBitPrivateApiInfo(
    key: String,
    secret: String,
    val timestamp: Long,
    val data: String,
    val receiveWindow: Int
) : PrivateApiInfo(key, secret) {
    fun source(): String =
        timestamp.toString() + key + receiveWindow.toString() + data
}