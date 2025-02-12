package com.newy.algotrade.notification_app.service

import com.newy.algotrade.common.exception.VerificationCodeException
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.port.`in`.VerifyNotificationAppInPort
import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VerifyNotificationAppCommandService(
    @Autowired private val findNotificationAppOutPort: FindNotificationAppOutPort,
    @Autowired private val saveNotificationAppOutPort: SaveNotificationAppOutPort,
) : VerifyNotificationAppInPort {
    @Transactional
    override suspend fun verify(command: VerifyNotificationAppCommand): Boolean {
        val notificationApp = findNotificationApp(command)
        val newNotificationApp = notificationApp
            .validate(webhookUrl = notificationApp.webhook.url)
            .verify(verifyCode = command.verifyCode)

        return saveNotificationAppOutPort.save(newNotificationApp)
    }

    private suspend fun findNotificationApp(command: VerifyNotificationAppCommand): NotificationApp =
        findNotificationAppOutPort.findByUserId(command.userId)
            ?: throw VerificationCodeException("알림 앱을 찾을 수 없습니다. (userId: ${command.userId})")
}