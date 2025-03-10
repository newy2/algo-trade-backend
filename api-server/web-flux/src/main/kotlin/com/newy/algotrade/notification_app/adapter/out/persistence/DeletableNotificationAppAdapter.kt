package com.newy.algotrade.notification_app.adapter.out.persistence

import com.newy.algotrade.notification_app.adapter.out.persistence.repository.DeletableUserNotificationAppR2dbcEntity
import com.newy.algotrade.notification_app.adapter.out.persistence.repository.DeletableUserNotificationAppR2dbcRepository
import com.newy.algotrade.notification_app.adapter.out.persistence.repository.DeletableUserNotificationAppVerifyCodeR2dbcRepository
import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import com.newy.algotrade.notification_app.port.out.DeleteNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.FindDeletableNotificationAppOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class DeletableNotificationAppAdapter(
    private val deletableUserNotificationAppR2dbcRepository: DeletableUserNotificationAppR2dbcRepository,
    private val deletableUserNotificationAppVerifyCodeR2dbcRepository: DeletableUserNotificationAppVerifyCodeR2dbcRepository,
) : FindDeletableNotificationAppOutPort, DeleteNotificationAppOutPort {
    override suspend fun findById(notificationAppId: Long): DeletableNotificationApp? =
        findNotificationApp(notificationAppId)?.toDomainModel()

    override suspend fun deleteById(notificationAppId: Long) {
        deletableUserNotificationAppVerifyCodeR2dbcRepository.deleteByUserNotificationAppId(notificationAppId)
        deletableUserNotificationAppR2dbcRepository.save(
            findNotificationApp(notificationAppId)!!.copy(
                useYn = "N"
            )
        )
    }

    private suspend fun findNotificationApp(notificationAppId: Long): DeletableUserNotificationAppR2dbcEntity? =
        deletableUserNotificationAppR2dbcRepository.findByIdAndUseYn(id = notificationAppId)
}