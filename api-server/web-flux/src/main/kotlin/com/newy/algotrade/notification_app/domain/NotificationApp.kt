package com.newy.algotrade.notification_app.domain

import com.newy.algotrade.common.exception.InitializedError
import java.time.LocalDateTime

data class Webhook(
    val type: String,
    val url: String,
) {
    fun validate(webhookUrl: String) {
        if (webhookUrl.isNotEmpty() && this.url != webhookUrl) {
            throw InitializedError("기존 Webhook URL 과 다릅니다. (${this.url} != $webhookUrl)")
        }
    }
}

data class NotificationApp(
    val userId: Long,
    val webhook: Webhook,
    val verifyCode: String = "",
    val expiredAt: LocalDateTime = getDefaultExpiredAt(),
    val isVerified: Boolean = false,
    private val codeGenerator: CodeGenerator = CodeGenerator.INSTANCE,
) {
    companion object {
        fun getDefaultExpiredAt(): LocalDateTime = LocalDateTime.now().plusMinutes(3)
    }

    fun generateVerifyCode(expiredAt: LocalDateTime = getDefaultExpiredAt()): NotificationApp {
        return copy(
            verifyCode = codeGenerator.generate(verifyCode),
            expiredAt = expiredAt,
        )
    }


    fun validate(webhookUrl: String = ""): NotificationApp {
        if (isVerified) {
            throw InitializedError("이미 검증 완료된 Webhook 입니다.")
        }

        webhook.validate(webhookUrl)
        return this
    }
}
