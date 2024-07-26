package com.newy.algotrade.web_flux.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationCommandPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationQueryPort
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SendNotificationSpringService(
    commandAdapter: SendNotificationCommandPort,
    queryAdapter: SendNotificationQueryPort,
    @Qualifier("createSendNotificationEventBus") eventBus: EventBus<SendNotificationEvent>,
    @Qualifier("slackHttpApiClient") httpApiClient: HttpApiClient
) : SendNotificationService(commandAdapter, queryAdapter, eventBus, httpApiClient)