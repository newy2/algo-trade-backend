package com.newy.algotrade.notification.adapter.out.external_system

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

@ExternalSystemAdapter
class SendNotificationMessageAdapter(
    @Autowired @Qualifier("slackHttpApiClient") private val slackApiClient: HttpApiClient
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