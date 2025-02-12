package com.newy.algotrade.notification_send.port.`in`

import com.newy.algotrade.notification_send.domain.NotificationSendMessage

fun interface SendMessageInPort {
    suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage
}