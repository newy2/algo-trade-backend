package com.newy.algotrade.web_flux.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.notification.SendNotificationLog
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogR2dbcEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository

@PersistenceAdapter
class SendNotificationLogAdapter(
    private val repository: SendNotificationLogRepository,
) : SendNotificationLogPort {
    override suspend fun getSendNotificationLog(sendNotificationLogId: Long) =
        repository.findByIdAsDomainEntity(sendNotificationLogId)
            ?: throw NotFoundRowException("sendNotificationLogId 를 찾을 수 없습니다. (id: ${sendNotificationLogId})")

    override suspend fun createByStatusRequested(notificationAppId: Long, requestMessage: String) =
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