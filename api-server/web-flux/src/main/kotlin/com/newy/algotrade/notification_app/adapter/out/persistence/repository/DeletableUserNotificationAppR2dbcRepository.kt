package com.newy.algotrade.notification_app.adapter.out.persistence.repository

import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DeletableUserNotificationAppR2dbcRepository :
    CoroutineCrudRepository<DeletableUserNotificationAppR2dbcEntity, Long> {
    suspend fun findByIdAndUseYn(id: Long, useYn: String = "Y"): DeletableUserNotificationAppR2dbcEntity?
}

@Table("user_notification_app")
data class DeletableUserNotificationAppR2dbcEntity(
    @Id val id: Long,
    val userId: Long,
    val useYn: String,
    @LastModifiedDate val updatedAt: LocalDateTime,
) {
    fun toDomainModel() = DeletableNotificationApp(
        id = id,
        userId = userId,
    )
}