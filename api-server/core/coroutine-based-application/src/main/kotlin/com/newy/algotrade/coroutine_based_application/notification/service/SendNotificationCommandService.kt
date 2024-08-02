package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.post
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.SendNotificationUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort
import com.newy.algotrade.domain.common.exception.NotFoundRowException

open class SendNotificationCommandService(
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
        getSendNotificationLog(event.sendNotificationLogId).statusProcessing()
            .also {
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

    private suspend fun getSendNotificationLog(sendNotificationLogId: Long) =
        adapter.getSendNotificationLog(sendNotificationLogId)
            ?: throw NotFoundRowException("sendNotificationLogId 를 찾을 수 없습니다. (id: ${sendNotificationLogId})")
}