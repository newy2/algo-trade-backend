package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.post
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.SendNotificationUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationPort

open class SendNotificationService(
    private val adapter: SendNotificationPort,
    private val eventBus: EventBus<SendNotificationEvent>,
    private val httpApiClient: HttpApiClient,
) : SendNotificationUseCase {
    override suspend fun requestSendNotification(command: SendNotificationCommand) {
        adapter.setStatusRequested(command.notificationAppId, command.requestMessage).let {
            eventBus.publishEvent(SendNotificationEvent(sendNotificationLogId = it))
        }
    }

    override suspend fun sendNotification(event: SendNotificationEvent) {
        adapter.putStatusProcessing(event.sendNotificationLogId)
        val responseMessage = adapter.getSendNotification(event.sendNotificationLogId).let { domainModel ->
            httpApiClient.post<String>(
                path = domainModel.path(),
                body = domainModel.body()
            )
        }
        adapter.putResponseMessage(event.sendNotificationLogId, responseMessage = responseMessage)
    }
}