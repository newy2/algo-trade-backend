package com.newy.algotrade.notification.port.out

import com.newy.algotrade.notification.domain.NotificationSendMessage

fun interface SendNotificationMessageOutPort {
    suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): String
}
