package com.newy.algotrade.setting.port.out

import com.newy.algotrade.setting.domain.NotificationApp

fun interface GetNotificationAppOutPort {
    suspend fun getNotificationApp(userId: Long): NotificationApp?
}