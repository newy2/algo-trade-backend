package com.newy.algotrade.notification.service

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.SendNotificationEvent
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.notification.port.`in`.SendNotificationUseCase
import com.newy.algotrade.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.notification.port.out.FindSendNotificationLogPort
import com.newy.algotrade.notification.port.out.SaveRequestedStatusSendNotificationLogPort
import com.newy.algotrade.notification.port.out.SaveSendNotificationLogPort
import com.newy.algotrade.notification.port.out.SendNotificationLogPort

open class SendNotificationCommandService(
    private val saveRequestedStatusSendNotificationLogPort: SaveRequestedStatusSendNotificationLogPort,
    private val findSendNotificationLogPort: FindSendNotificationLogPort,
    private val saveSendNotificationLogPort: SaveSendNotificationLogPort,
    private val eventBus: EventBus<SendNotificationEvent>,
    private val httpApiClient: HttpApiClient,
) : SendNotificationUseCase {
    constructor(
        sendNotificationLogPort: SendNotificationLogPort,
        eventBus: EventBus<SendNotificationEvent>,
        httpApiClient: HttpApiClient,
    ) : this(
        saveRequestedStatusSendNotificationLogPort = sendNotificationLogPort,
        findSendNotificationLogPort = sendNotificationLogPort,
        saveSendNotificationLogPort = sendNotificationLogPort,
        eventBus = eventBus,
        httpApiClient = httpApiClient,
    )

    override suspend fun requestSendNotification(command: SendNotificationCommand) {
        saveRequestedStatusSendNotificationLogPort.saveSendNotificationLog(
            command.notificationAppId,
            command.requestMessage
        ).let {
            eventBus.publishEvent(SendNotificationEvent(sendNotificationLogId = it))
        }
    }

    override suspend fun sendNotification(event: SendNotificationEvent): Unit =
        getSendNotificationLog(event.sendNotificationLogId).statusProcessing()
            .also {
                saveSendNotificationLogPort.saveSendNotificationLog(it)
            }.let {
                it.responseMessage(
                    responseMessage = httpApiClient.post<String>(
                        path = it.getUrlPath(),
                        body = it.getHttpRequestBody()
                    )
                )
            }.let {
                saveSendNotificationLogPort.saveSendNotificationLog(it)
            }

    private suspend fun getSendNotificationLog(sendNotificationLogId: Long) =
        findSendNotificationLogPort.findSendNotificationLog(sendNotificationLogId)
            ?: throw NotFoundRowException("sendNotificationLogId 를 찾을 수 없습니다. (id: ${sendNotificationLogId})")
}