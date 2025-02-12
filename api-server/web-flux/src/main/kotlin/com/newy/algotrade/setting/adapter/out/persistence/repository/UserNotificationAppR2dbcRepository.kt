package com.newy.algotrade.setting.adapter.out.persistence.repository

import com.newy.algotrade.setting.domain.NotificationApp
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository("UserNotificationAppR2dbcRepositoryForSettingPackage")
interface UserNotificationAppR2dbcRepository : CoroutineCrudRepository<UserNotificationAppR2dbcEntity, Long> {
    suspend fun findByUserIdAndUseYn(userId: Long, useYn: String = "Y"): UserNotificationAppR2dbcEntity?
}

@Table("user_notification_app")
data class UserNotificationAppR2dbcEntity(
    @Id val id: Long,
    val userId: Long,
    val type: String,
    val url: String,
    val useYn: String,
) {
    fun toDomainModel(notificationVerifyCode: UserNotificationAppVerifyCodeR2dbcEntity) = NotificationApp(
        id = id,
        webhookType = type,
        webhookUrl = url,
        isVerified = notificationVerifyCode.verifyYn == "Y",
        verifyCodeExpiredAt = notificationVerifyCode.expiredAt,
    )
}