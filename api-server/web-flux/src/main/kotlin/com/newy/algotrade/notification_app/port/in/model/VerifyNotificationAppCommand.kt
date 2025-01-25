package com.newy.algotrade.notification_app.port.`in`.model

data class VerifyNotificationAppCommand(
    val userId: Long,
    val verifyCode: String
)