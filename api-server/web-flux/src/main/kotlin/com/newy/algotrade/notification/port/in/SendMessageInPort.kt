package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.notification.domain.NotificationSendMessage

fun interface SendMessageInPort {
    suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage
}