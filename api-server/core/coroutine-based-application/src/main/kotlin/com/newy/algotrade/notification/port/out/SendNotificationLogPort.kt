package com.newy.algotrade.notification.port.out

import com.newy.algotrade.notification.domain.SendNotificationLog

interface SendNotificationLogPort :
    FindSendNotificationLogPort,
    SaveSendNotificationLogPort,
    SaveRequestedStatusSendNotificationLogPort

fun interface FindSendNotificationLogPort {
    suspend fun findSendNotificationLog(sendNotificationLogId: Long): SendNotificationLog?
}

fun interface SaveSendNotificationLogPort {
    suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean
}

fun interface SaveRequestedStatusSendNotificationLogPort {
    suspend fun saveSendNotificationLog(notificationAppId: Long, requestMessage: String): Long
}