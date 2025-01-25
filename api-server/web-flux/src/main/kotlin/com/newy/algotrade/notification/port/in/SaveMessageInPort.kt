package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.notification.domain.NotificationSendMessage

fun interface SaveMessageInPort {
    suspend fun saveMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage
}