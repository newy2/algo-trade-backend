package com.newy.algotrade.domain.common.consts

object GlobalEnv {
    private val envVariables: MutableMap<String, String> = mutableMapOf()

    val BY_BIT_WEB_URL: String by lazy { envVariables.getValue("BY_BIT_WEB_URL") }
    val BY_BIT_WEB_SOCKET_URL: String by lazy { envVariables.getValue("BY_BIT_WEB_SOCKET_URL") }

    val LS_SEC_WEB_URL: String by lazy { envVariables.getValue("LS_SEC_WEB_URL") }
    val LS_SEC_WEB_SOCKET_URL: String by lazy { envVariables.getValue("LS_SEC_WEB_SOCKET_URL") }
    val LS_SEC_API_KEY: String by lazy { envVariables.getValue("LS_SEC_API_KEY") }
    val LS_SEC_API_SECRET: String by lazy { envVariables.getValue("LS_SEC_API_SECRET") }

    fun initialize(
        byBitWebUrl: String,
        byBitWebSocketUrl: String,
        lsSecWebUrl: String,
        lsSecWebSocketUrl: String,
        lsSecApiKey: String,
        lsSecApiSecret: String,
    ) {
        envVariables["BY_BIT_WEB_URL"] = byBitWebUrl
        envVariables["BY_BIT_WEB_SOCKET_URL"] = byBitWebSocketUrl

        envVariables["LS_SEC_WEB_URL"] = lsSecWebUrl
        envVariables["LS_SEC_WEB_SOCKET_URL"] = lsSecWebSocketUrl
        envVariables["LS_SEC_API_KEY"] = lsSecApiKey
        envVariables["LS_SEC_API_SECRET"] = lsSecApiSecret
    }
}