package com.newy.algotrade.coroutine_based_application.notification.port.`in`

import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand

interface SendNotificationUseCase {
    suspend fun requestSendNotification(command: SendNotificationCommand)
    suspend fun sendNotification(event: SendNotificationEvent)
}
