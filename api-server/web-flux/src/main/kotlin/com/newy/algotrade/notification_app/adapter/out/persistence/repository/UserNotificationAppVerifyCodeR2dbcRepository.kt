package com.newy.algotrade.notification_app.adapter.out.persistence.repository

import com.newy.algotrade.notification_app.domain.NotificationApp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserNotificationAppVerifyCodeR2dbcRepository :
    CoroutineCrudRepository<UserNotificationAppVerifyCodeR2dbcEntity, Long> {
    suspend fun findFirstByUserNotificationAppIdOrderByIdDesc(userNotificationAppId: Long): UserNotificationAppVerifyCodeR2dbcEntity?
}

@Table("user_notification_app_verify_code")
data class UserNotificationAppVerifyCodeR2dbcEntity(
    @Id val id: Long = 0,
    val userNotificationAppId: Long = 0,
    val verifyCode: String = "",
    val verifyYn: String = "",
    val expiredAt: LocalDateTime = NotificationApp.getDefaultExpiredAt(),
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
) {
    fun update(userNotificationAppId: Long, domainModel: NotificationApp) = this.copy(
        userNotificationAppId = userNotificationAppId,
        verifyCode = domainModel.verifyCode,
        verifyYn = if (domainModel.isVerified) "Y" else "N",
        expiredAt = domainModel.expiredAt,
    )
}