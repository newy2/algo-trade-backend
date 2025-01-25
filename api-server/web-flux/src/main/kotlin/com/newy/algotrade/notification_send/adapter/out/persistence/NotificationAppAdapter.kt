package com.newy.algotrade.notification_send.adapter.out.persistence

import com.newy.algotrade.notification_send.adapter.out.persistence.repository.UserNotificationAppR2dbcRepository
import com.newy.algotrade.notification_send.adapter.out.persistence.repository.UserNotificationAppVerifyCodeR2dbcRepository
import com.newy.algotrade.notification_send.domain.NotificationApp
import com.newy.algotrade.notification_send.domain.Webhook
import com.newy.algotrade.notification_send.port.out.FindNotificationAppOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter
import org.springframework.beans.factory.annotation.Autowired

@PersistenceAdapter("NotificationAppAdapterForNotificationPackage")
class NotificationAppAdapter(
    @Autowired private val notificationAppRepository: UserNotificationAppR2dbcRepository,
    @Autowired private val notificationVerifyCodeRepository: UserNotificationAppVerifyCodeR2dbcRepository,
) : FindNotificationAppOutPort {
    override suspend fun findNotificationApp(
        userId: Long,
        isVerified: Boolean
    ): NotificationApp? =
        notificationAppRepository.findByUserIdAndUseYn(
            userId = userId,
            useYn = "Y"
        )?.let { notificationApp ->
            notificationVerifyCodeRepository.findByUserNotificationAppIdAndVerifyYn(
                userNotificationAppId = notificationApp.id,
                verifyYn = if (isVerified) "Y" else "N"
            )?.let {
                NotificationApp(
                    id = notificationApp.id,
                    webhook = Webhook.from(
                        type = notificationApp.type,
                        url = notificationApp.url,
                    )
                )
            }
        }
}