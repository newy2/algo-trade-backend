package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification

interface SendNotificationQueryPort {
    suspend fun getSendNotification(notificationLogId: Long): SendNotification
}