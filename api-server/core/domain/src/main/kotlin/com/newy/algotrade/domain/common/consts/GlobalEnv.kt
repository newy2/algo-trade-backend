package com.newy.algotrade.domain.common.consts

object GlobalEnv {
    private val envVariables: MutableMap<String, Any> = mutableMapOf()

    val BY_BIT_WEB_URL: String by lazy { envVariables.getValue("BY_BIT_WEB_URL") as String }
    val BY_BIT_WEB_SOCKET_URL: String by lazy { envVariables.getValue("BY_BIT_WEB_SOCKET_URL") as String }

    val LS_SEC_WEB_URL: String by lazy { envVariables.getValue("LS_SEC_WEB_URL") as String }
    val LS_SEC_WEB_SOCKET_URL: String by lazy { envVariables.getValue("LS_SEC_WEB_SOCKET_URL") as String }
    val LS_SEC_API_KEY: String by lazy { envVariables.getValue("LS_SEC_API_KEY") as String }
    val LS_SEC_API_SECRET: String by lazy { envVariables.getValue("LS_SEC_API_SECRET") as String }

    val ADMIN_USER_ID: Long by lazy { envVariables.getValue("ADMIN_USER_ID") as Long }

    fun initialize(
        byBitWebUrl: String,
        byBitWebSocketUrl: String,
        lsSecWebUrl: String,
        lsSecWebSocketUrl: String,
        lsSecApiKey: String,
        lsSecApiSecret: String,
        adminUserId: Long,
    ) {
        envVariables["BY_BIT_WEB_URL"] = byBitWebUrl
        envVariables["BY_BIT_WEB_SOCKET_URL"] = byBitWebSocketUrl

        envVariables["LS_SEC_WEB_URL"] = lsSecWebUrl
        envVariables["LS_SEC_WEB_SOCKET_URL"] = lsSecWebSocketUrl
        envVariables["LS_SEC_API_KEY"] = lsSecApiKey
        envVariables["LS_SEC_API_SECRET"] = lsSecApiSecret

        initializeAdminUserId(adminUserId)
    }

    fun initializeAdminUserId(adminUserId: Long) {
        envVariables["ADMIN_USER_ID"] = adminUserId
    }
}