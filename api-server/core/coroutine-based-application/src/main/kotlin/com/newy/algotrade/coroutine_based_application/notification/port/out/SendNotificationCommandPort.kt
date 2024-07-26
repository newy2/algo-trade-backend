package com.newy.algotrade.coroutine_based_application.notification.port.out

interface SendNotificationCommandPort {
    suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long
    suspend fun putStatusProcessing(sendNotificationLogId: Long): Boolean
    suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String): Boolean
}