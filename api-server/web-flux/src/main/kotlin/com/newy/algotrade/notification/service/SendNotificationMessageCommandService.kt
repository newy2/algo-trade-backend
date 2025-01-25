package com.newy.algotrade.notification.service

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.port.`in`.GetMessageInPort
import com.newy.algotrade.notification.port.`in`.SaveMessageInPort
import com.newy.algotrade.notification.port.`in`.SendMessageInPort
import com.newy.algotrade.notification.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.notification.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification.port.out.SaveNotificationSendMessageOutPort
import com.newy.algotrade.notification.port.out.SendNotificationMessageOutPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendNotificationMessageCommandService(
    @Autowired private val findNotificationAppOutPort: FindNotificationAppOutPort,
    @Autowired private val sendNotificationMessageOutPort: SendNotificationMessageOutPort,
    @Autowired private val saveNotificationSendMessageOutPort: SaveNotificationSendMessageOutPort
) : GetMessageInPort, SendMessageInPort, SaveMessageInPort {
    @Transactional(readOnly = true)
    override suspend fun getMessage(command: SendNotificationMessageCommand) =
        NotificationSendMessage(
            notificationApp = getNotificationApp(command),
            requestMessage = command.message,
        )

    override suspend fun sendMessage(message: NotificationSendMessage) =
        try {
            message.succeed(sendNotificationMessageOutPort.sendMessage(message))
        } catch (e: HttpResponseException) {
            message.failed(e.responseMessage)
        }

    @Transactional
    override suspend fun saveMessage(notificationSendMessage: NotificationSendMessage) =
        notificationSendMessage.copy(
            id = saveNotificationSendMessageOutPort.saveNotificationSendMessage(notificationSendMessage)
        )

    private suspend fun getNotificationApp(command: SendNotificationMessageCommand) =
        findNotificationAppOutPort.findNotificationApp(
            userId = command.userId,
            isVerified = command.isVerified,
        ) ?: throw NotFoundRowException(
            "사용자 알림앱 정보를 찾을 수 없습니다. (userId: ${command.userId}, isVerified: ${command.isVerified})"
        )
}
