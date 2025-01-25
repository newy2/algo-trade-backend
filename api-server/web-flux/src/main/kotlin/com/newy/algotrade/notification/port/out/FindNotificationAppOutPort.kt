package com.newy.algotrade.notification.port.out

import com.newy.algotrade.notification.domain.NotificationApp

fun interface FindNotificationAppOutPort {
    suspend fun findNotificationApp(userId: Long, isVerified: Boolean): NotificationApp?
}