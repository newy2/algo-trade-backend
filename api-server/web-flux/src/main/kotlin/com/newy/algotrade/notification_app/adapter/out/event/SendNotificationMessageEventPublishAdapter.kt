package com.newy.algotrade.notification_app.adapter.out.event

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.spring.annotation.EventPublisherAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

@EventPublisherAdapter
class SendNotificationMessageEventPublishAdapter(
    @Autowired private val publisher: ApplicationEventPublisher,
) : SendNotificationMessageOutPort {
    override suspend fun send(event: SendNotificationMessageEvent) {
        publisher.publishEvent(event)
    }
}