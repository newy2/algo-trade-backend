package com.newy.algotrade.web_flux.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.NotificationAppPort
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppRepository

@PersistenceAdapter
class NotificationAppAdapter(
    private val repository: NotificationAppRepository,
) : NotificationAppPort {
    override suspend fun hasNotificationApp(userId: Long): Boolean {
        return repository.existsByUserId(userId)
    }

    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean =
        repository.save(NotificationAppEntity(command)).id > 0
}