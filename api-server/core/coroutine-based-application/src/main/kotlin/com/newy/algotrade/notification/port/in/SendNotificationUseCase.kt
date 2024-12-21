package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.common.event.SendNotificationEvent
import com.newy.algotrade.notification.port.`in`.model.SendNotificationCommand

interface SendNotificationUseCase :
    RequestSendNotificationUseCase,
    ProcessSendNotificationUseCase

fun interface RequestSendNotificationUseCase {
    suspend fun requestSendNotification(command: SendNotificationCommand)
}

fun interface ProcessSendNotificationUseCase {
    suspend fun sendNotification(event: SendNotificationEvent)
}