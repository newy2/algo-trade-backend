package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.port.`in`.model.SendNotificationMessageCommand

fun interface GetMessageInPort {
    suspend fun getMessage(command: SendNotificationMessageCommand): NotificationSendMessage
}