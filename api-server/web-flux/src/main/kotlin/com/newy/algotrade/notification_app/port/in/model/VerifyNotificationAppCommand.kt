package com.newy.algotrade.notification_app.port.`in`.model

import com.newy.algotrade.common.helper.SelfValidating
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class VerifyNotificationAppCommand(
    @field:Min(1) val userId: Long,
    @field:NotBlank val verifyCode: String
) : SelfValidating() {
    init {
        validate()
    }
}