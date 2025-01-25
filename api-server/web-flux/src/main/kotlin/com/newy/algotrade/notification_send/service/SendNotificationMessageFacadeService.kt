package com.newy.algotrade.notification_send.service

import com.newy.algotrade.notification_send.port.`in`.GetMessageInPort
import com.newy.algotrade.notification_send.port.`in`.SaveMessageInPort
import com.newy.algotrade.notification_send.port.`in`.SendMessageInPort
import com.newy.algotrade.notification_send.port.`in`.SendNotificationMessageInPort
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SendNotificationMessageFacadeService(
    @Autowired private val getMessageInPort: GetMessageInPort,
    @Autowired private val sendMessageInPort: SendMessageInPort,
    @Autowired private val saveMessageInPort: SaveMessageInPort,
) : SendNotificationMessageInPort {
    override suspend fun sendNotificationMessage(command: SendNotificationMessageCommand) {
        getMessageInPort.getMessage(command).let {
            sendMessageInPort.sendMessage(it)
        }.let {
            saveMessageInPort.saveMessage(it)
        }
    }
}