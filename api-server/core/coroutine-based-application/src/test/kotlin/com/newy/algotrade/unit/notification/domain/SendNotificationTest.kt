package com.newy.algotrade.unit.notification.domain

import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification
import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.consts.SlackNotificationRequestMessageFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SendNotificationTest {
    private val domainModel = SendNotification(
        notificationApp = NotificationApp.SLACK,
        url = "${NotificationApp.SLACK.host}/111/222",
        requestMessage = "message",
    )

    @Test
    fun path() {
        assertEquals("/111/222", domainModel.path())
    }

    @Test
    fun body() {
        assertEquals(
            SlackNotificationRequestMessageFormat.from("message"),
            domainModel.body()
        )
    }
}