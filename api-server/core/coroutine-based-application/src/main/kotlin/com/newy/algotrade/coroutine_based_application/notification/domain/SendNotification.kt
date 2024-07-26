package com.newy.algotrade.coroutine_based_application.notification.domain

import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.consts.NotificationRequestMessageFormat

data class SendNotification(
    private val notificationApp: NotificationApp,
    private val url: String,
    private val requestMessage: String,
) {
    fun path(): String {
        return notificationApp.getPath(url)
    }

    fun body(): NotificationRequestMessageFormat {
        return notificationApp.getRequestMessageFormat(requestMessage)
    }
}