package com.newy.algotrade.notification.port.`in`.model

import com.newy.algotrade.domain.common.helper.SelfValidating
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SendNotificationCommand(
    @field:Min(1) val notificationAppId: Long,
    @field:NotBlank val requestMessage: String
) : SelfValidating() {
    init {
        validate()
    }
}
