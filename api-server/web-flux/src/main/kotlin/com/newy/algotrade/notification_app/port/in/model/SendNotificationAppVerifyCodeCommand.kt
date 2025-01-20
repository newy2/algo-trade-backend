package com.newy.algotrade.notification_app.port.`in`.model

import com.newy.algotrade.common.annotation.ForTesting
import com.newy.algotrade.common.helper.SelfValidating
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

data class SendNotificationAppVerifyCodeCommand(
    @field:Min(1) val userId: Long,
    @field:Pattern(regexp = "SLACK") val webhookType: String,
    @field:Pattern(regexp = "^https://hooks.slack.com/services/.*") val webhookUrl: String,
) : SelfValidating() {
    init {
        validate()
    }

    fun toDomainModel(@ForTesting expiredAt: LocalDateTime = NotificationApp.getDefaultExpiredAt()) = NotificationApp(
        userId = userId,
        webhook = Webhook(
            type = webhookType,
            url = webhookUrl,
        ),
        expiredAt = expiredAt,
    )
}
