package com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository

import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus
import com.newy.algotrade.domain.notification.SendNotificationLog
import io.r2dbc.spi.Row
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface SendNotificationLogRepository : CoroutineCrudRepository<SendNotificationLogR2dbcEntity, Long> {
    @Query(
        """
        SELECT sunl.id               as send_notification_log_id
             , una.id                as notification_app_id
             , una.type              as notification_app
             , sunl.status           as status
             , una.url               as url
             , sunl.request_message  as request_message
             , sunl.response_message as response_message
        FROM   send_user_notification_log sunl
        INNER JOIN user_notification_app una on una.id = sunl.user_notification_app_id
        WHERE  sunl.id = :sendNotificationLogId
    """
    )
    suspend fun findByIdAsDomainEntity(sendNotificationLogId: Long): SendNotificationLog?
}

@Table("send_user_notification_log")
data class SendNotificationLogR2dbcEntity(
    @Id val id: Long = 0,
    @Column("user_notification_app_id") val notificationAppId: Long,
    val requestMessage: String = "",
    val responseMessage: String? = null,
    val status: String = "",
    @LastModifiedDate val updatedAt: LocalDateTime? = null
) {
    constructor(domainEntity: SendNotificationLog) : this(
        id = domainEntity.sendNotificationLogId,
        notificationAppId = domainEntity.notificationAppId,
        requestMessage = domainEntity.requestMessage,
        responseMessage = domainEntity.responseMessage,
        status = domainEntity.status.name,
    )
}

@ReadingConverter
class SendNotificationLogReadingConverter : Converter<Row, SendNotificationLog> {
    override fun convert(source: Row) = SendNotificationLog(
        sendNotificationLogId = source["send_notification_log_id"] as Long,
        notificationAppId = source["notification_app_id"] as Long,
        notificationAppType = NotificationAppType.valueOf(source["notification_app"] as String),
        status = SendNotificationLogStatus.valueOf(source["status"] as String),
        url = source["url"] as String,
        requestMessage = source["request_message"] as String,
        responseMessage = source["response_message"] as String?,
    )
}