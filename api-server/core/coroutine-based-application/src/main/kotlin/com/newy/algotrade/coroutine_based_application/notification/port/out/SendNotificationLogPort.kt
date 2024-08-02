package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.domain.notification.SendNotificationLog

interface SendNotificationLogPort : SendNotificationLogQueryPort, SendNotificationLogCommandPort

interface SendNotificationLogQueryPort {
    suspend fun getSendNotificationLog(sendNotificationLogId: Long): SendNotificationLog?
}

interface SendNotificationLogCommandPort {
    suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean
    suspend fun createByStatusRequested(notificationAppId: Long, requestMessage: String): Long
}