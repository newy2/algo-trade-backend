package com.newy.algotrade.notification_send.port.out

import com.newy.algotrade.notification_send.domain.NotificationSendMessage

fun interface SendNotificationMessageOutPort {
    suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): String
}
