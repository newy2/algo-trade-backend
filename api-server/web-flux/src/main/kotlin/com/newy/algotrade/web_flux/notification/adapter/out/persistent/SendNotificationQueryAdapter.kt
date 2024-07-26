package com.newy.algotrade.web_flux.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationQueryPort
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository

@PersistenceAdapter
class SendNotificationQueryAdapter(
    private val sendNotificationLogRepository: SendNotificationLogRepository,
) : SendNotificationQueryPort {
    override suspend fun getSendNotification(sendNootificationLogId: Long) =
        sendNotificationLogRepository.findByIdAsSendNotification(sendNotificationId = sendNootificationLogId)
            ?: throw NotFoundRowException("notificationLogId 를 찾을 수 없습니다. (id: ${sendNootificationLogId})")
}