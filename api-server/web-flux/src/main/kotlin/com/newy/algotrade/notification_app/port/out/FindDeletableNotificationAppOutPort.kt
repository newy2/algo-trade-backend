package com.newy.algotrade.notification_app.port.out

import com.newy.algotrade.notification_app.domain.DeletableNotificationApp

fun interface FindDeletableNotificationAppOutPort {
    suspend fun findById(notificationAppId: Long): DeletableNotificationApp?
}