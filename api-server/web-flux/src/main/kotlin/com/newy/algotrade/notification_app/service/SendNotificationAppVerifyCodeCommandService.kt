package com.newy.algotrade.notification_app.service

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.port.`in`.SendNotificationAppVerifyCodeInPort
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.spring.hook.useTransactionHook
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendNotificationAppVerifyCodeCommandService(
    private val findNotificationAppOutPort: FindNotificationAppOutPort,
    private val saveNotificationAppOutPort: SaveNotificationAppOutPort,
    private val sendNotificationMessageOutPort: SendNotificationMessageOutPort,
) : SendNotificationAppVerifyCodeInPort {
    @Transactional
    override suspend fun sendVerifyCode(command: SendNotificationAppVerifyCodeCommand): String {
        val newNotificationApp = findNotificationApp(command)
            .validate(command.webhookUrl)
            .generateVerifyCode()

        saveNotificationAppOutPort.save(newNotificationApp)
        useTransactionHook(
            onAfterCommit = {
                sendNotificationMessageOutPort.send(
                    SendNotificationMessageEvent(
                        userId = command.userId,
                        message = "인증코드: ${newNotificationApp.verifyCode}",
                        isVerified = false,
                    )
                )
            }
        )

        return newNotificationApp.verifyCode
    }

    private suspend fun findNotificationApp(command: SendNotificationAppVerifyCodeCommand): NotificationApp =
        findNotificationAppOutPort.findByUserId(command.userId) ?: command.toDomainModel()
}
