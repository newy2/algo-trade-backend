package com.newy.algotrade.notification_app.adapter.out.persistence.repository

import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeletableUserNotificationAppVerifyCodeR2dbcRepository :
    CoroutineCrudRepository<DeletableUserNotificationAppVerifyCodeR2dbcEntity, Long> {
    suspend fun deleteByUserNotificationAppId(userNotificationAppId: Long)
}

@Table("user_notification_app_verify_code")
data class DeletableUserNotificationAppVerifyCodeR2dbcEntity(
    val userNotificationAppId: Long,
)