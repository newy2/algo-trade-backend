package com.newy.algotrade.coroutine_based_application.auth.adpter.out.local.model

import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo

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