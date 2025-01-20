package com.newy.algotrade.notification_app.port.out

import com.newy.algotrade.notification_app.domain.NotificationApp

fun interface FindNotificationAppOutPort {
    suspend fun findByUserId(userId: Long): NotificationApp?
}
