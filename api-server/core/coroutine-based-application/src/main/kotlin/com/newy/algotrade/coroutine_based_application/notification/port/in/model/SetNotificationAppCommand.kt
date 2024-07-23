package com.newy.algotrade.coroutine_based_application.notification.port.`in`.model

import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.helper.SelfValidating
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SetNotificationAppCommand(
    @field:Min(1) val userId: Long,
    val type: NotificationApp,
    @field:NotBlank val url: String,
) : SelfValidating() {
    init {
        validate()
        type.validateUrl(url)
    }
}