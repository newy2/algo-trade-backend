package com.newy.algotrade.notification.domain

import com.newy.algotrade.common.domain.consts.NotificationAppType

data class NotificationApp(
    val id: Long = 0,
    val userId: Long,
    val type: NotificationAppType,
    val url: String,
)