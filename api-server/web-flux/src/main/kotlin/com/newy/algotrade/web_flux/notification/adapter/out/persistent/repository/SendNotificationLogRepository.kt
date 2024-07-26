package com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository

import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface SendNotificationLogRepository : CoroutineCrudRepository<SendNotificationLogEntity, Long> {
    @Query(
        """
        SELECT una.type as notification_app
             , una.url
             , sunl.request_message
        FROM   send_user_notification_log sunl
        INNER JOIN user_notification_app una on una.id = sunl.user_notification_app_id
        WHERE  sunl.id = :sendNotificationId
    """
    )
    suspend fun findByIdAsSendNotification(sendNotificationId: Long): SendNotification?
}

@Table("send_user_notification_log")
data class SendNotificationLogEntity(
    @Id val id: Long = 0,
    @Column("user_notification_app_id") val notificationAppId: Long,
    val requestMessage: String = "",
    val responseMessage: String? = null,
    val status: String = "",
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null
) {
    enum class Status(displayName: String) {
        REQUESTED("요청완료"),
        PROCESSING("처리중"),
        SUCCEED("전송완료"),
        FAILED("전송실패")
    }
}