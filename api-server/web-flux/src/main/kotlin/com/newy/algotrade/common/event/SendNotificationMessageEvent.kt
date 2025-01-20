package com.newy.algotrade.common.event

data class SendNotificationMessageEvent(
    val userId: Long,
    val message: String,
)