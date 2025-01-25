package com.newy.algotrade.notification.port.out

import com.newy.algotrade.notification.domain.NotificationSendMessage

fun interface SaveNotificationSendMessageOutPort {
    suspend fun saveNotificationSendMessage(notificationSendMessage: NotificationSendMessage): Long
}