package com.newy.algotrade.notification_send.adapter.out.persistence.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository("UserNotificationAppVerifyCodeR2dbcRepositoryForNotificationPackage")
interface UserNotificationAppVerifyCodeR2dbcRepository :
    CoroutineCrudRepository<UserNotificationAppVerifyCodeR2dbcEntity, Long> {
    suspend fun findByUserNotificationAppIdAndVerifyYn(
        userNotificationAppId: Long,
        verifyYn: String
    ): UserNotificationAppVerifyCodeR2dbcEntity?
}

@Table("user_notification_app_verify_code")
data class UserNotificationAppVerifyCodeR2dbcEntity(
    @Id val id: Long = 0,
    val userNotificationAppId: Long = 0,
    val verifyYn: String,
)