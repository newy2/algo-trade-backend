package com.newy.algotrade.notification_app.port.`in`.model

data class DeleteNotificationAppCommand(
    val userId: Long,
    val notificationAppId: Long,
)