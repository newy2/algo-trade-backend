package com.newy.algotrade.notification_app.port.out

fun interface DeleteNotificationAppOutPort {
    suspend fun deleteById(notificationAppId: Long)
}