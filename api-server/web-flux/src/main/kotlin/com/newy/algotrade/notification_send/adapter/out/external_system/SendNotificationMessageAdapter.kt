package com.newy.algotrade.notification_send.adapter.out.external_system

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter
import org.springframework.beans.factory.annotation.Qualifier

@ExternalSystemAdapter
class SendNotificationMessageAdapter(
    @Qualifier("slackHttpApiClient") private val slackApiClient: HttpApiClient
) : SendNotificationMessageOutPort {
    override suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): String {
        val response = slackApiClient.post<String>(
            path = notificationSendMessage.getUrlPath(),
            body = notificationSendMessage.getRequestBody(),
        )
        if (response != "ok") {
            throw HttpResponseException(response)
        }

        return response
    }
}