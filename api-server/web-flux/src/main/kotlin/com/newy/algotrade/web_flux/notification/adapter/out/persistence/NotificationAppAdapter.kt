package com.newy.algotrade.web_flux.notification.adapter.out.persistence

import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.port.out.NotificationAppPort
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.NotificationAppR2dbcEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.NotificationAppRepository

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