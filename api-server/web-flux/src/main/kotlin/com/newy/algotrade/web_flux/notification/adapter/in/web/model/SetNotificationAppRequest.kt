package com.newy.algotrade.web_flux.notification.adapter.`in`.web.model

import com.newy.algotrade.common.domain.consts.GlobalEnv
import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.common.domain.helper.SelfValidating
import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand
import jakarta.validation.constraints.Pattern

data class SetNotificationAppRequest(
    @field:Pattern(regexp = "SLACK") val type: String,
    val url: String,
) : SelfValidating() {
    init {
        validate()
    }

    fun toIncomingPortModel() =
        SetNotificationAppCommand(
            userId = GlobalEnv.ADMIN_USER_ID,
            type = NotificationAppType.valueOf(type),
            url = url,
        )
}