package com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository

import com.newy.algotrade.notification.domain.NotificationApp
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NotificationAppRepository : CoroutineCrudRepository<NotificationAppR2dbcEntity, Long> {
    suspend fun existsByUserId(userId: Long): Boolean
}

@Table("user_notification_app")
data class NotificationAppR2dbcEntity(
    @Id val id: Long = 0,
    @Column("users_id") val userId: Long,
    val type: String = "",
    val url: String = "",
) {
    constructor(domainEntity: NotificationApp) : this(
        userId = domainEntity.userId,
        type = domainEntity.type.name,
        url = domainEntity.url
    )
}