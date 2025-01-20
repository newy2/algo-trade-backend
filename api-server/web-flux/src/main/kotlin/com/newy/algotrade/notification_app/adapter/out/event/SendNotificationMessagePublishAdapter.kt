package com.newy.algotrade.notification_app.adapter.out.event

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import org.springframework.stereotype.Component

@Component
class SendNotificationMessagePublishAdapter : SendNotificationMessageOutPort {
    override suspend fun send(event: SendNotificationMessageEvent) {
//        TODO("Not yet implemented")
    }
}