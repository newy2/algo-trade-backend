package com.newy.algotrade.web_flux.notification.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus
import com.newy.algotrade.domain.notification.SendNotificationLog
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.SendNotificationLogR2dbcEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.SendNotificationLogRepository

@PersistenceAdapter
class SendNotificationLogAdapter(
    private val repository: SendNotificationLogRepository,
) : SendNotificationLogPort {
    override suspend fun getSendNotificationLog(sendNotificationLogId: Long) =
        repository.findByIdAsDomainEntity(sendNotificationLogId)

    override suspend fun createSendNotificationLog(notificationAppId: Long, requestMessage: String) =
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