package com.newy.algotrade.notification.adapter.out.persistence

import com.newy.algotrade.notification.adapter.out.persistence.repository.NotificationAppR2dbcEntity
import com.newy.algotrade.notification.adapter.out.persistence.repository.NotificationAppRepository
import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.port.out.NotificationAppPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NotificationAppAdapter(
    private val repository: NotificationAppRepository,
) : NotificationAppPort {
    override suspend fun existsNotificationApp(userId: Long): Boolean {
        return repository.existsByUserId(userId)
    }

    override suspend fun saveNotificationApp(domainEntity: NotificationApp): Boolean =
        repository.save(NotificationAppR2dbcEntity(domainEntity)).id > 0
}