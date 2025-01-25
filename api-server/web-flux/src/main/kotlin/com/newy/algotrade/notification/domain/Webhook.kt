package com.newy.algotrade.notification.domain

abstract class Webhook(
    private val host: String,
    private val url: String,
) {
    companion object {
        fun from(type: String, url: String): Webhook {
            return when (type) {
                "SLACK" -> Slack(url = url)
                else -> throw IllegalArgumentException("Unknown webhook type: $type")
            }
        }
    }

    fun getUrlPath() = url.replace(host, "")
    abstract fun getRequestBody(requestMessage: String): Map<String, Any>
}

data class Slack(val url: String) : Webhook(host = HOST, url = url) {
    companion object {
        const val HOST = "https://hooks.slack.com"
    }

    override fun getRequestBody(requestMessage: String) = mapOf(
        "blocks" to listOf(
            mapOf(
                "type" to "section",
                "text" to mapOf(
                    "type" to "mrkdwn",
                    "text" to requestMessage
                )
            )
        )
    )
}