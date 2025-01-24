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
interface UserNotificationAppR2dbcRepository : CoroutineCrudRepository<UserNotificationAppR2dbcEntity, Long> {
    suspend fun findByUserIdAndUseYn(userId: Long, useYn: String): UserNotificationAppR2dbcEntity?
}

@Table("user_notification_app")
data class UserNotificationAppR2dbcEntity(
    @Id val id: Long = 0,
    val userId: Long = 0,
    val type: String = "",
    val url: String = "",
    val useYn: String = "Y",
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
) {
    fun update(domainModel: NotificationApp) = this.copy(
        userId = domainModel.userId,
        type = domainModel.webhook.type,
        url = domainModel.webhook.url,
    )
}