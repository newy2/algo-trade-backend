package com.newy.algotrade.notification_send.port.`in`

import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand

fun interface SendNotificationMessageInPort {
    suspend fun sendNotificationMessage(command: SendNotificationMessageCommand)
}