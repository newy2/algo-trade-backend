package com.newy.algotrade.notification_send.port.`in`

import com.newy.algotrade.notification_send.domain.NotificationSendMessage

fun interface SaveMessageInPort {
    suspend fun saveMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage
}