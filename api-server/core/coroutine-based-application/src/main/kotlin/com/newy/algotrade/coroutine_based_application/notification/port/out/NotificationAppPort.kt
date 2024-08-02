package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.domain.notification.NotificationApp

interface NotificationAppPort : NotificationAppQueryPort, NotificationAppCommandPort

interface NotificationAppQueryPort {
    suspend fun hasNotificationApp(userId: Long): Boolean
}

interface NotificationAppCommandPort {
    suspend fun setNotificationApp(domainEntity: NotificationApp): Boolean
}