package com.newy.algotrade.notification.port.out

import com.newy.algotrade.notification.domain.NotificationApp

interface NotificationAppPort :
    ExistsHasNotificationAppPort,
    SaveNotificationAppPort

fun interface ExistsHasNotificationAppPort {
    suspend fun existsNotificationApp(userId: Long): Boolean
}

fun interface SaveNotificationAppPort {
    suspend fun saveNotificationApp(domainEntity: NotificationApp): Boolean
}