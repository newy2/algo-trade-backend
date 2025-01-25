package com.newy.algotrade.notification_send.domain

data class NotificationSendMessage(
    val id: Long = 0,
    val notificationApp: NotificationApp,
    val requestMessage: String,
    val responseMessage: String = "",
    val status: Status = Status.SENDING,
) {
    enum class Status {
        SENDING,
        SUCCEED,
        FAILED,
    }

    fun succeed(responseMessage: String) = copy(
        responseMessage = responseMessage,
        status = Status.SUCCEED,
    )

    fun failed(responseMessage: String) = copy(
        responseMessage = responseMessage,
        status = Status.FAILED,
    )

    fun getRequestBody() = notificationApp.getRequestBody(requestMessage)

    fun getUrlPath() = notificationApp.getUrlPath()
}
