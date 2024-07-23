package com.newy.algotrade.domain.common.consts

enum class NotificationApp(
    val baseUrl: String,
) {
    SLACK("https://hooks.slack.com/services/");

    fun validateUrl(url: String) {
        if (!url.startsWith(baseUrl)) {
            throw IllegalArgumentException("URL 은 '$baseUrl' 으로 시작해야 합니다")
        }
    }
}