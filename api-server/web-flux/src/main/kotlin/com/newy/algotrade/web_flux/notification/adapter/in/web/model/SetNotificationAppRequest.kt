package com.newy.algotrade.web_flux.notification.adapter.`in`.web.model

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.domain.common.helper.SelfValidating
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