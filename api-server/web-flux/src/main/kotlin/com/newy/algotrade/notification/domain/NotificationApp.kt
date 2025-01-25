package com.newy.algotrade.notification.domain

data class NotificationApp(
    val id: Long,
    val webhook: Webhook,
) {
    fun getUrlPath() = webhook.getUrlPath()
    fun getRequestBody(requestMessage: String) = webhook.getRequestBody(requestMessage)
}
