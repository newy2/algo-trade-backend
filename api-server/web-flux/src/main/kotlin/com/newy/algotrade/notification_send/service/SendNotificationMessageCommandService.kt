package com.newy.algotrade.notification_send.service

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.notification_send.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_send.port.out.SaveNotificationSendMessageOutPort
import com.newy.algotrade.notification_send.port.out.SendNotificationMessageOutPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendNotificationMessageCommandService(
    private val findNotificationAppOutPort: FindNotificationAppOutPort,
    private val sendNotificationMessageOutPort: SendNotificationMessageOutPort,
    private val saveNotificationSendMessageOutPort: SaveNotificationSendMessageOutPort
) {
    @Transactional(readOnly = true)
    suspend fun getMessage(command: SendNotificationMessageCommand) =
        NotificationSendMessage(
            notificationApp = getNotificationApp(command),
            requestMessage = command.message,
        )

    suspend fun sendMessage(message: NotificationSendMessage) =
        try {
            message.succeed(sendNotificationMessageOutPort.sendMessage(message))
        } catch (e: HttpResponseException) {
            message.failed(e.responseMessage)
        }

    @Transactional
    suspend fun saveMessage(notificationSendMessage: NotificationSendMessage) =
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
