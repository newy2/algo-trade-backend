package com.newy.algotrade.notification_send.port.out

import com.newy.algotrade.notification_send.domain.NotificationApp

fun interface FindNotificationAppOutPort {
    suspend fun findNotificationApp(userId: Long, isVerified: Boolean): NotificationApp?
}