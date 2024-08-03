package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.domain.notification.NotificationApp

interface NotificationAppPort :
    HasNotificationAppPort,
    SetNotificationAppPort

fun interface HasNotificationAppPort {
    suspend fun hasNotificationApp(userId: Long): Boolean
}

fun interface SetNotificationAppPort {
    suspend fun setNotificationApp(domainEntity: NotificationApp): Boolean
}