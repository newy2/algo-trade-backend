package com.newy.algotrade.coroutine_based_application.notification.port.out

interface HasNotificationAppPort {
    suspend fun hasNotificationApp(userId: Long): Boolean
}