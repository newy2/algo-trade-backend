package com.newy.algotrade.notification_app.adapter.`in`.web.model

import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand

data class DeleteNotificationAppRequest(
    val userId: Long,
    val notificationAppId: Long,
) {
    fun toInPortModel() = DeleteNotificationAppCommand(
        userId = userId,
        notificationAppId = notificationAppId,
    )
}
