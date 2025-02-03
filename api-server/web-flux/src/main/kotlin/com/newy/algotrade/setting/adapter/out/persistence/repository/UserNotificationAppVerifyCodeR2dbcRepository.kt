package com.newy.algotrade.setting.adapter.out.persistence.repository

import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository("UserNotificationAppVerifyCodeR2dbcRepositoryForSettingPackage")
interface UserNotificationAppVerifyCodeR2dbcRepository :
    CoroutineCrudRepository<UserNotificationAppVerifyCodeR2dbcEntity, Long> {
    suspend fun findByUserNotificationAppId(userNotificationAppId: Long): UserNotificationAppVerifyCodeR2dbcEntity
}

@Table("user_notification_app_verify_code")
data class UserNotificationAppVerifyCodeR2dbcEntity(
    val userNotificationAppId: Long,
    val verifyYn: String,
    val expiredAt: LocalDateTime,
)