package com.newy.algotrade.domain.auth

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ByBitPrivateApiInfo(
    key: String,
    secret: String,
    val timestamp: Long,
    val data: String,
    val receiveWindow: Int
) : PrivateApiInfo(key, secret) {
    companion object {
        private const val ALGORITHM = "HmacSHA256"
    }

    private fun source(): String =
        timestamp.toString() + key + receiveWindow.toString() + data

    fun accessToken(): String =
        Mac.getInstance(ALGORITHM).let {
            it.init(SecretKeySpec(secret.toByteArray(), ALGORITHM))
            val bytes = it.doFinal(source().toByteArray())
            HexFormat.of().formatHex(bytes)
        }
}