package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.post
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.SendNotificationUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationCommandPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationQueryPort

open class SendNotificationService(
    private val commandAdapter: SendNotificationCommandPort,
    private val queryAdapter: SendNotificationQueryPort,
    private val eventBus: EventBus<SendNotificationEvent>,
    private val httpApiClient: HttpApiClient,
) : SendNotificationUseCase {
    override suspend fun requestSendNotification(command: SendNotificationCommand) {
        commandAdapter.setStatusRequested(command.notificationAppId, command.requestMessage).let {
            eventBus.publishEvent(SendNotificationEvent(sendNotificationLogId = it))
        }
    }

    override suspend fun sendNotification(event: SendNotificationEvent) {
        commandAdapter.putStatusProcessing(event.sendNotificationLogId)
        val responseMessage = queryAdapter.getSendNotification(event.sendNotificationLogId).let { domainModel ->
            httpApiClient.post<String>(
                path = domainModel.path(),
                body = domainModel.body()
            )
        }
        commandAdapter.putResponseMessage(event.sendNotificationLogId, responseMessage = responseMessage)
    }
}