package com.newy.algotrade.notification.domain

import com.newy.algotrade.common.consts.NotificationAppType
import com.newy.algotrade.common.consts.NotificationRequestMessageFormat
import com.newy.algotrade.common.consts.SendNotificationLogStatus
import com.newy.algotrade.common.consts.SendNotificationLogStatus.*
import com.newy.algotrade.common.domain.exception.PreconditionError

data class SendNotificationLog(
    val sendNotificationLogId: Long,
    val notificationAppId: Long,
    val notificationAppType: NotificationAppType,
    val status: SendNotificationLogStatus,
    val url: String,
    val requestMessage: String,
    val responseMessage: String? = null,
) {
    fun getUrlPath(): String {
        return notificationAppType.getPath(url)
    }

    fun getHttpRequestBody(): NotificationRequestMessageFormat {
        return notificationAppType.getRequestMessageFormat(requestMessage)
    }

    fun statusProcessing(): SendNotificationLog {
        if (status != REQUESTED) {
            throw PreconditionError("REQUESTED 상태만 변경 가능합니다. (status: ${status.name})")
        }

        return copy(status = PROCESSING)
    }

    fun responseMessage(responseMessage: String): SendNotificationLog {
        if (status != PROCESSING) {
            throw PreconditionError("PROCESSING 상태만 변경 가능합니다. (status: ${status.name})")
        }

        return copy(
            responseMessage = responseMessage,
            status = if (responseMessage.lowercase() == "ok") SUCCEED else FAILED
        )
    }
}