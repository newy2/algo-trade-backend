package com.newy.algotrade.notification.port.`in`.model

import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.domain.common.helper.SelfValidating
import com.newy.algotrade.domain.notification.NotificationApp
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SetNotificationAppCommand(
    @field:Min(1) val userId: Long,
    val type: NotificationAppType,
    @field:NotBlank val url: String,
) : SelfValidating() {
    init {
        validate()
        type.validateUrl(url)
    }

    fun toDomainEntity() =
        NotificationApp(
            userId = userId,
            type = type,
            url = url,
        )
}