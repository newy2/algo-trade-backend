package com.newy.algotrade.notification_app.adapter.out.persistence

import com.newy.algotrade.notification_app.adapter.out.persistence.repository.UserNotificationAppR2dbcEntity
import com.newy.algotrade.notification_app.adapter.out.persistence.repository.UserNotificationAppR2dbcRepository
import com.newy.algotrade.notification_app.adapter.out.persistence.repository.UserNotificationAppVerifyCodeR2dbcEntity
import com.newy.algotrade.notification_app.adapter.out.persistence.repository.UserNotificationAppVerifyCodeR2dbcRepository
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NotificationAppPersistenceAdapter(
    @Autowired private val userNotificationAppR2dbcRepository: UserNotificationAppR2dbcRepository,
    @Autowired private val userNotificationAppVerifyCodeR2dbcRepository: UserNotificationAppVerifyCodeR2dbcRepository,
) : FindNotificationAppOutPort, SaveNotificationAppOutPort {
    override suspend fun findByUserId(userId: Long): NotificationApp? =
        findUserNotificationApp(userId)?.let { userNotificationApp ->
            findUserNotificationAppVerifyCode(userNotificationApp.id)?.let { userNotificationAppVerifyCode ->
                NotificationApp(
                    userId = userNotificationApp.userId,
                    webhook = Webhook(
                        type = userNotificationApp.type,
                        url = userNotificationApp.url,
                    ),
                    isVerified = userNotificationAppVerifyCode.verifyYn == "Y",
                    verifyCode = userNotificationAppVerifyCode.verifyCode,
                    expiredAt = userNotificationAppVerifyCode.expiredAt,
                )
            }
        }

    override suspend fun save(domainModel: NotificationApp): Boolean =
        findUserNotificationApp(domainModel).let {
            userNotificationAppR2dbcRepository.save(it.update(domainModel))
        }.let { userNotificationApp ->
            findUserNotificationAppVerifyCode(userNotificationApp).let {
                userNotificationAppVerifyCodeR2dbcRepository.save(
                    it.update(
                        userNotificationAppId = userNotificationApp.id,
                        domainModel = domainModel,
                    )
                )
            }
        }.let {
            true
        }

    private suspend fun findUserNotificationApp(domainModel: NotificationApp) =
        findUserNotificationApp(domainModel.userId) ?: UserNotificationAppR2dbcEntity()

    private suspend fun findUserNotificationAppVerifyCode(userNotificationApp: UserNotificationAppR2dbcEntity) =
        findUserNotificationAppVerifyCode(userNotificationApp.id) ?: UserNotificationAppVerifyCodeR2dbcEntity()

    private suspend fun findUserNotificationApp(userId: Long) =
        userNotificationAppR2dbcRepository.findByUserIdAndUseYn(
            userId = userId,
            useYn = "Y"
        )

    private suspend fun findUserNotificationAppVerifyCode(userNotificationAppId: Long) =
        userNotificationAppVerifyCodeR2dbcRepository.findFirstByUserNotificationAppIdOrderByIdDesc(
            userNotificationAppId = userNotificationAppId
        )
}