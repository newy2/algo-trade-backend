package com.newy.algotrade.notification.adapter.out.persistence

import com.newy.algotrade.common.annotation.PersistenceAdapter
import com.newy.algotrade.common.domain.consts.SendNotificationLogStatus
import com.newy.algotrade.notification.adapter.out.persistence.repository.SendNotificationLogR2dbcEntity
import com.newy.algotrade.notification.adapter.out.persistence.repository.SendNotificationLogRepository
import com.newy.algotrade.notification.domain.SendNotificationLog
import com.newy.algotrade.notification.port.out.SendNotificationLogPort

@PersistenceAdapter
class SendNotificationLogAdapter(
    private val repository: SendNotificationLogRepository,
) : SendNotificationLogPort {
    override suspend fun findSendNotificationLog(sendNotificationLogId: Long) =
        repository.findByIdAsDomainEntity(sendNotificationLogId)

    override suspend fun saveSendNotificationLog(notificationAppId: Long, requestMessage: String) =
        repository.save(
            SendNotificationLogR2dbcEntity(
                notificationAppId = notificationAppId,
                requestMessage = requestMessage,
                status = SendNotificationLogStatus.REQUESTED.name
            )
        ).id

    override suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog) =
        repository.save(SendNotificationLogR2dbcEntity(domainEntity)).id > 0
}