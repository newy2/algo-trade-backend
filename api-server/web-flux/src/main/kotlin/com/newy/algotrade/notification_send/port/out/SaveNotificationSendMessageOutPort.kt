package com.newy.algotrade.notification_send.port.out

import com.newy.algotrade.notification_send.domain.NotificationSendMessage

fun interface SaveNotificationSendMessageOutPort {
    suspend fun saveNotificationSendMessage(notificationSendMessage: NotificationSendMessage): Long
}