package com.newy.algotrade.notification_send.adapter.out.persistence

import com.newy.algotrade.notification_send.adapter.out.persistence.repository.UserNotificationSendMessageR2dbcR2dbcEntity
import com.newy.algotrade.notification_send.adapter.out.persistence.repository.UserNotificationSendMessageR2dbcRepository
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.port.out.SaveNotificationSendMessageOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NotificationSendMessageAdapter(
    private val repository: UserNotificationSendMessageR2dbcRepository
) : SaveNotificationSendMessageOutPort {
    override suspend fun saveNotificationSendMessage(notificationSendMessage: NotificationSendMessage): Long =
        repository.save(
            UserNotificationSendMessageR2dbcR2dbcEntity(
                domainModel = notificationSendMessage
            )
        ).id
}