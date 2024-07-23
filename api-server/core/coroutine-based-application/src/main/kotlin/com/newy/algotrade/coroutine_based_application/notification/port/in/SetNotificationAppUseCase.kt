package com.newy.algotrade.coroutine_based_application.notification.port.`in`

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand

interface SetNotificationAppUseCase {
    suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean
}