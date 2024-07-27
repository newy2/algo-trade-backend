package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification

interface SendNotificationPort : SendNotificationQueryPort, SendNotificationCommandPort

interface SendNotificationQueryPort {
    suspend fun getSendNotification(notificationLogId: Long): SendNotification
}

interface SendNotificationCommandPort {
    suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long
    suspend fun putStatusProcessing(sendNotificationLogId: Long): Boolean
    suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String): Boolean
}