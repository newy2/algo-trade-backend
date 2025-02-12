package com.newy.algotrade.auth.domain

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ByBitPrivateApiInfo(
    val privateApiInfo: PrivateApiInfo,
    val timestamp: Long,
    val data: String,
    val receiveWindow: Int
) {
    companion object {
        private const val ALGORITHM = "HmacSHA256"
    }

    private fun getSource(): String =
        timestamp.toString() + privateApiInfo.appKey + receiveWindow.toString() + data

    fun getAccessToken(): String =
        Mac.getInstance(ALGORITHM).let {
            it.init(
                SecretKeySpec(
                    privateApiInfo.appSecret.toByteArray(),
                    ALGORITHM
                )
            )
            val bytes = it.doFinal(getSource().toByteArray())
            HexFormat.of().formatHex(bytes)
        }

    fun getRequestHeaders(): Map<String, String> =
        mapOf(
            "X-BAPI-SIGN" to getAccessToken(),
            "X-BAPI-API-KEY" to privateApiInfo.appKey,
            "X-BAPI-TIMESTAMP" to timestamp.toString(),
            "X-BAPI-RECV-WINDOW" to "5000",
        )
}