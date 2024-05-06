package com.newy.algotrade.coroutine_based_application.auth.adpter.out.local

import com.newy.algotrade.coroutine_based_application.auth.adpter.out.local.model.ByBitPrivateApiInfo
import com.newy.algotrade.coroutine_based_application.auth.port.out.GetAccessTokenPort
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ByBitAccessTokenCalculator : GetAccessTokenPort<ByBitPrivateApiInfo> {
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