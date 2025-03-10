package com.newy.algotrade.setting.adapter.out.persistence

import com.newy.algotrade.setting.adapter.out.persistence.repository.UserNotificationAppR2dbcRepository
import com.newy.algotrade.setting.adapter.out.persistence.repository.UserNotificationAppVerifyCodeR2dbcRepository
import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.port.out.GetNotificationAppOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NotificationAppAdapter(
    private val userNotificationAppR2dbcRepository: UserNotificationAppR2dbcRepository,
    private val userNotificationAppVerifyCodeR2dbcRepository: UserNotificationAppVerifyCodeR2dbcRepository,
) : GetNotificationAppOutPort {
    override suspend fun getNotificationApp(userId: Long): NotificationApp? {
        val notificationApp = userNotificationAppR2dbcRepository.findByUserIdAndUseYn(userId) ?: return null
        val notificationVerifyCode =
            userNotificationAppVerifyCodeR2dbcRepository.findByUserNotificationAppId(notificationApp.id)

        return notificationApp.toDomainModel(notificationVerifyCode)
    }
}