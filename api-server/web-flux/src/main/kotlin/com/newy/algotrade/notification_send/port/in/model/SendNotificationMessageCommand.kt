package com.newy.algotrade.notification_send.port.`in`.model

import com.newy.algotrade.common.helper.SelfValidating
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SendNotificationMessageCommand(
    @field:Min(1) val userId: Long,
    @field:NotBlank val message: String,
    val isVerified: Boolean,
) : SelfValidating() {
    init {
        validate()
    }
}