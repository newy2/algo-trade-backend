package com.newy.algotrade.coroutine_based_application.auth.bybit

import com.newy.algotrade.coroutine_based_application.auth.AccessTokenApi
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ByBitAccessTokenCalculator : AccessTokenApi<ByBitPrivateApiInfo> {
    companion object {
        private const val ALGORITHM = "HmacSHA256"
    }

    override suspend fun accessToken(info: ByBitPrivateApiInfo): String =
        Mac.getInstance(ALGORITHM).let {
            it.init(SecretKeySpec(info.secret.toByteArray(), ALGORITHM))
            val bytes = it.doFinal(info.source().toByteArray())
            HexFormat.of().formatHex(bytes)
        }
}