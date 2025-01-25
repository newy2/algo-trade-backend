package com.newy.algotrade.notification_send.port.`in`

import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand

fun interface GetMessageInPort {
    suspend fun getMessage(command: SendNotificationMessageCommand): NotificationSendMessage
}