package com.newy.algotrade.notification.service

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.SendNotificationEvent
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.notification.port.out.SendNotificationLogPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringSendNotificationCommandService(
    adapter: SendNotificationLogPort,
    @Qualifier("sendNotificationEventBus") eventBus: EventBus<SendNotificationEvent>,
    @Qualifier("slackHttpApiClient") httpApiClient: HttpApiClient
) : SendNotificationCommandService(adapter, eventBus, httpApiClient)