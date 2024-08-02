package com.newy.algotrade.domain.notification

import com.newy.algotrade.domain.common.consts.NotificationAppType

data class NotificationApp(
    val id: Long = 0,
    val userId: Long,
    val type: NotificationAppType,
    val url: String,
)