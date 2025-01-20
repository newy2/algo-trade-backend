package com.newy.algotrade.notification_app.port.out

import com.newy.algotrade.common.event.SendNotificationMessageEvent

fun interface SendNotificationMessageOutPort {
    suspend fun send(event: SendNotificationMessageEvent)
}
