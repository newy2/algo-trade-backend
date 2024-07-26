package com.newy.algotrade.domain.common.consts

enum class NotificationApp(
    val host: String,
) {
    SLACK("https://hooks.slack.com") {
        override fun getRequestMessageFormat(text: String): NotificationRequestMessageFormat =
            SlackNotificationRequestMessageFormat.from(text)
    };

    fun validateUrl(url: String) {
        if (!url.startsWith(host)) {
            throw IllegalArgumentException("URL 은 '$host' 으로 시작해야 합니다")
        }
    }

    fun getPath(url: String) = url.replace(host, "")
    abstract fun getRequestMessageFormat(text: String): NotificationRequestMessageFormat
}

interface NotificationRequestMessageFormat

data class SlackNotificationRequestMessageFormat(
    val blocks: List<SlackBlock>
) : NotificationRequestMessageFormat {
    companion object {
        fun from(text: String) =
            SlackNotificationRequestMessageFormat(listOf(SlackBlock(SlackText(text))))
    }

    data class SlackBlock(
        val text: SlackText,
        val type: String = "section",
    )

    data class SlackText(
        val text: String,
        val type: String = "mrkdwn",
    )
}