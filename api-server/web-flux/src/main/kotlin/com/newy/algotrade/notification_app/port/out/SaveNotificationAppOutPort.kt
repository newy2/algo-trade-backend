package com.newy.algotrade.notification_app.port.out

import com.newy.algotrade.notification_app.domain.NotificationApp

fun interface SaveNotificationAppOutPort {
    suspend fun save(app: NotificationApp): Boolean
}