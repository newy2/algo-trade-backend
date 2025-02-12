package com.newy.algotrade.notification_send.adapter.out.persistence.repository

import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserNotificationSendMessageR2dbcRepository :
    CoroutineCrudRepository<UserNotificationSendMessageR2dbcR2dbcEntity, Long>

@Table("user_notification_send_message")
data class UserNotificationSendMessageR2dbcR2dbcEntity(
    @Id val id: Long = 0,
    val userNotificationAppId: Long = 0,
    val requestMessage: String = "",
    val responseMessage: String = "",
    val status: String = "",
) {
    constructor(domainModel: NotificationSendMessage) : this(
        userNotificationAppId = domainModel.notificationApp.id,
        requestMessage = domainModel.requestMessage,
        responseMessage = domainModel.responseMessage,
        status = domainModel.status.toString()
    )
}