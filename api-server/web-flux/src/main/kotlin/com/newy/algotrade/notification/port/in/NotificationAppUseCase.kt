package com.newy.algotrade.notification.port.`in`

import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand

interface NotificationAppUseCase
    : SetNotificationAppUseCase

fun interface SetNotificationAppUseCase {
    suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean
}