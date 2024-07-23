package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand

interface SetNotificationAppPort {
    suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean
}