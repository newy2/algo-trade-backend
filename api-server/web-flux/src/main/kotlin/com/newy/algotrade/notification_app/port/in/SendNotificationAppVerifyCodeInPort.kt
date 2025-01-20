package com.newy.algotrade.notification_app.port.`in`

import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand

fun interface SendNotificationAppVerifyCodeInPort {
    suspend fun sendVerifyCode(command: SendNotificationAppVerifyCodeCommand): String
}