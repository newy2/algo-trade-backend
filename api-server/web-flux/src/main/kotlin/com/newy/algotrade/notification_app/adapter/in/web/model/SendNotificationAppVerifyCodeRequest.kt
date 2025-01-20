package com.newy.algotrade.notification_app.adapter.`in`.web.model

import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand

data class SendNotificationAppVerifyCodeRequest(
    val webhookUrl: String,
    private val type: String,
) {
    fun toInPortModel(userId: Long) = SendNotificationAppVerifyCodeCommand(
        userId = userId,
        webhookType = type,
        webhookUrl = webhookUrl,
    )
}
