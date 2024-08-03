package com.newy.algotrade.coroutine_based_application.notification.port.out

import com.newy.algotrade.domain.notification.SendNotificationLog

interface SendNotificationLogPort :
    GetSendNotificationLogPort,
    SaveSendNotificationLogPort,
    CreateSendNotificationLogPort

fun interface GetSendNotificationLogPort {
    suspend fun getSendNotificationLog(sendNotificationLogId: Long): SendNotificationLog?
}

fun interface SaveSendNotificationLogPort {
    suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean
}

fun interface CreateSendNotificationLogPort {
    suspend fun createSendNotificationLog(notificationAppId: Long, requestMessage: String): Long
}