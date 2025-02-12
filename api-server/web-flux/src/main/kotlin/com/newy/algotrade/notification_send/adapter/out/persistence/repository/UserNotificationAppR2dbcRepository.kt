package com.newy.algotrade.notification_send.adapter.out.persistence.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository("UserNotificationAppR2dbcRepositoryForNotificationPackage")
interface UserNotificationAppR2dbcRepository : CoroutineCrudRepository<UserNotificationAppR2dbcEntity, Long> {
    suspend fun findByUserIdAndUseYn(userId: Long, useYn: String): UserNotificationAppR2dbcEntity?
}

@Table("user_notification_app")
data class UserNotificationAppR2dbcEntity(
    @Id val id: Long = 0,
    val userId: Long = 0,
    val useYn: String = "",
    val type: String = "",
    val url: String = "",
)