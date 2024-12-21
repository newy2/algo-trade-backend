package com.newy.algotrade.common.domain.consts

object GlobalEnv {
    private val envVariables: MutableMap<String, Any> = mutableMapOf()

    val BY_BIT_WEB_URL: String by lazy { GlobalEnv.envVariables.getValue("BY_BIT_WEB_URL") as String }
    val BY_BIT_WEB_SOCKET_URL: String by lazy { GlobalEnv.envVariables.getValue("BY_BIT_WEB_SOCKET_URL") as String }

    val LS_SEC_WEB_URL: String by lazy { GlobalEnv.envVariables.getValue("LS_SEC_WEB_URL") as String }
    val LS_SEC_WEB_SOCKET_URL: String by lazy { GlobalEnv.envVariables.getValue("LS_SEC_WEB_SOCKET_URL") as String }
    val LS_SEC_API_KEY: String by lazy { GlobalEnv.envVariables.getValue("LS_SEC_API_KEY") as String }
    val LS_SEC_API_SECRET: String by lazy { GlobalEnv.envVariables.getValue("LS_SEC_API_SECRET") as String }

    val ADMIN_USER_ID: Long by lazy { GlobalEnv.envVariables.getValue("ADMIN_USER_ID") as Long }

    fun initialize(
        byBitWebUrl: String,
        byBitWebSocketUrl: String,
        lsSecWebUrl: String,
        lsSecWebSocketUrl: String,
        lsSecApiKey: String,
        lsSecApiSecret: String,
        adminUserId: Long,
    ) {
        GlobalEnv.envVariables["BY_BIT_WEB_URL"] = byBitWebUrl
        GlobalEnv.envVariables["BY_BIT_WEB_SOCKET_URL"] = byBitWebSocketUrl

        GlobalEnv.envVariables["LS_SEC_WEB_URL"] = lsSecWebUrl
        GlobalEnv.envVariables["LS_SEC_WEB_SOCKET_URL"] = lsSecWebSocketUrl
        GlobalEnv.envVariables["LS_SEC_API_KEY"] = lsSecApiKey
        GlobalEnv.envVariables["LS_SEC_API_SECRET"] = lsSecApiSecret

        GlobalEnv.initializeAdminUserId(adminUserId)
    }

    fun initializeAdminUserId(adminUserId: Long) {
        GlobalEnv.envVariables["ADMIN_USER_ID"] = adminUserId
    }
}