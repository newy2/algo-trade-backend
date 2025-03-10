package com.newy.algotrade.notification_app.service

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendNotificationAppVerifyCodeCommandService(
    private val findNotificationAppOutPort: FindNotificationAppOutPort,
    private val saveNotificationAppOutPort: SaveNotificationAppOutPort,
    private val sendNotificationMessageOutPort: SendNotificationMessageOutPort,
) {
    @Transactional
    suspend fun saveVerifyCode(command: SendNotificationAppVerifyCodeCommand): NotificationApp {
        val newNotificationApp = findNotificationApp(command)
            .validate(command.webhookUrl)
            .generateVerifyCode()

        saveNotificationAppOutPort.save(newNotificationApp)

        return newNotificationApp
    }

    suspend fun sendVerifyCode(userId: Long, notificationApp: NotificationApp) {
        sendNotificationMessageOutPort.send(
            SendNotificationMessageEvent(
                userId = userId,
                message = "인증코드: ${notificationApp.verifyCode}",
                isVerified = false,
            )
        )
    }

    private suspend fun findNotificationApp(command: SendNotificationAppVerifyCodeCommand): NotificationApp =
        findNotificationAppOutPort.findByUserId(command.userId) ?: command.toDomainModel()
}