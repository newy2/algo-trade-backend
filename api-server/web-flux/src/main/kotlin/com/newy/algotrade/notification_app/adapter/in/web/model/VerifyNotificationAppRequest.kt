package com.newy.algotrade.notification_app.adapter.`in`.web.model

import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand

data class VerifyNotificationAppRequest(
    private val verifyCode: String,
) {
    fun toInPortModel(userId: Long) = VerifyNotificationAppCommand(
        userId = userId,
        verifyCode = verifyCode
    )
}