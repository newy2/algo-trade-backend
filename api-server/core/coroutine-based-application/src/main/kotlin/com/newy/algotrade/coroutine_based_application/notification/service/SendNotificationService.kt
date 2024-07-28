package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.post
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.SendNotificationUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort

open class SendNotificationService(
    private val adapter: SendNotificationLogPort,
    private val eventBus: EventBus<SendNotificationEvent>,
    private val httpApiClient: HttpApiClient,
) : SendNotificationUseCase {
    override suspend fun requestSendNotification(command: SendNotificationCommand) {
        adapter.createByStatusRequested(command.notificationAppId, command.requestMessage).let {
            eventBus.publishEvent(SendNotificationEvent(sendNotificationLogId = it))
        }
    }

    override suspend fun sendNotification(event: SendNotificationEvent): Unit =
        adapter.getSendNotificationLog(event.sendNotificationLogId)
            .let {
                it.statusProcessing()
            }.also {
                adapter.saveSendNotificationLog(it)
            }.let {
                it.responseMessage(
                    responseMessage = httpApiClient.post<String>(
                        path = it.getUrlPath(),
                        body = it.getHttpRequestBody()
                    )
                )
            }.let {
                adapter.saveSendNotificationLog(it)
            }
}