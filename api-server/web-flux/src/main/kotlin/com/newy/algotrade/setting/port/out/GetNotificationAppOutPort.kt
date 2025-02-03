package com.newy.algotrade.setting.port.out

import com.newy.algotrade.setting.domain.NotificationApp

fun interface GetNotificationAppOutPort {
    fun getNotificationApp(userId: Long): NotificationApp?
}