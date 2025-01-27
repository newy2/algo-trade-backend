package com.newy.algotrade.notification_app.port.`in`.model

import com.newy.algotrade.common.helper.SelfValidating
import jakarta.validation.constraints.Min

data class DeleteNotificationAppCommand(
    @field:Min(1) val userId: Long,
    @field:Min(1) val notificationAppId: Long,
) : SelfValidating() {
    init {
        validate()
    }
}