package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.notification.port.`in`.model.SendNotificationMessageCommand

fun interface SendNotificationMessageInPort {
    suspend fun sendNotificationMessage(command: SendNotificationMessageCommand)
}