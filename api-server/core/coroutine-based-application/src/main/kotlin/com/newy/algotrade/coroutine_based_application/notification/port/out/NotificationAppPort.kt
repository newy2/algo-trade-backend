package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand

interface NotificationAppPort : NotificationAppCommandPort, NotificationAppQueryPort

interface NotificationAppQueryPort {
    suspend fun hasNotificationApp(userId: Long): Boolean
}

interface NotificationAppCommandPort {
    suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean
}