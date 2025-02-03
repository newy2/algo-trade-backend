package com.newy.algotrade.setting.domain

import java.time.LocalDateTime

data class NotificationApp(
    val id: Long,
    val webhookType: String,
    val webhookUrl: String,
    val isVerified: Boolean,
    val verifyCodeExpiredAt: LocalDateTime
)