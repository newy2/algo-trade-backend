package com.newy.algotrade.notification_send.service

import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.port.`in`.SendNotificationMessageInPort
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import org.springframework.stereotype.Service

@Service
class SendNotificationMessageFacadeService(
    private val commandService: SendNotificationMessageCommandService,
) : SendNotificationMessageInPort {
    override suspend fun sendNotificationMessage(command: SendNotificationMessageCommand): NotificationSendMessage? {
        return commandService.getMessage(command)
            .let {
                commandService.sendMessage(it)
            }.let {
                commandService.saveMessage(it)
            }
    }
}