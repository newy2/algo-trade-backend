package com.newy.algotrade.notification_app.domain

import com.newy.algotrade.common.exception.VerificationCodeException
import java.time.LocalDateTime

data class Webhook(
    val type: String,
    val url: String,
) {
    fun validate(webhookUrl: String) {
        if (webhookUrl.isNotEmpty() && this.url != webhookUrl) {
            throw VerificationCodeException("기존 Webhook URL 과 다릅니다. (${this.url} != $webhookUrl)")
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
            throw VerificationCodeException("이미 검증 완료된 Webhook 입니다.")
        }

        webhook.validate(webhookUrl)
        return this
    }

    fun verify(verifyCode: String, now: LocalDateTime = LocalDateTime.now()): NotificationApp {
        if (expiredAt < now) {
            throw VerificationCodeException("검증 가능 시간을 초과했습니다.")
        }

        if (this.verifyCode != verifyCode) {
            throw VerificationCodeException("인증 코드가 다릅니다.")
        }

        return copy(isVerified = true)
    }
}
