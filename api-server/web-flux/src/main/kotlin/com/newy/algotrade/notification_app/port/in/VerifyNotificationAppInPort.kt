package com.newy.algotrade.notification_app.port.`in`

import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand

fun interface VerifyNotificationAppInPort {
    suspend fun verify(command: VerifyNotificationAppCommand): Boolean
}