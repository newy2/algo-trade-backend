package com.newy.algotrade.notification_app.service

import com.newy.algotrade.notification_app.port.`in`.SendNotificationAppVerifyCodeInPort
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import org.springframework.stereotype.Service

@Service
class SendNotificationAppVerifyCodeFacadeService(
    private val commandService: SendNotificationAppVerifyCodeCommandService,
) : SendNotificationAppVerifyCodeInPort {
    override suspend fun saveAndSendVerifyCode(command: SendNotificationAppVerifyCodeCommand): String {
        val newNotificationApp = commandService.saveVerifyCode(command)
        commandService.sendVerifyCode(userId = command.userId, notificationApp = newNotificationApp)
        return newNotificationApp.verifyCode
    }
}