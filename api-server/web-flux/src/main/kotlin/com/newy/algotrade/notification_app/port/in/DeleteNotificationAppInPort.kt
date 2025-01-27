package com.newy.algotrade.notification_app.port.`in`

import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand

fun interface DeleteNotificationAppInPort {
    suspend fun deleteNotificationApp(command: DeleteNotificationAppCommand)
}