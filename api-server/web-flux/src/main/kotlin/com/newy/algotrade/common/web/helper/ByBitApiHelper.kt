package com.newy.algotrade.common.web.helper

import com.newy.algotrade.auth.domain.ByBitPrivateApiInfo
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.HttpApiRateLimit
import com.newy.algotrade.common.web.http.get
import com.newy.algotrade.market_account.adapter.out.external_system.model.ByBitGetWalletBalanceHttpResponse
import java.time.Instant

class ByBitApiHelper(
    private val client: HttpApiClient,
) : ApiHelper() {
    companion object {
        private const val GET_WALLET_BALANCE_PATH = "/v5/account/wallet-balance"
    }

    init {
        rateLimits.put(GET_WALLET_BALANCE_PATH, HttpApiRateLimit(delayMillis = 500))
    }

    override suspend fun isValidPrivateApiInfo(privateApiInfo: PrivateApiInfo): Boolean {
        awaitRateLimit(GET_WALLET_BALANCE_PATH)

        val params = mapOf(
            "accountType" to "UNIFIED",
        )
        try {
            val response = client.get<ByBitGetWalletBalanceHttpResponse>(
                path = GET_WALLET_BALANCE_PATH,
                headers = getPrivateApiHeaders(
                    privateApiInfo = privateApiInfo,
                    data = params.toQueryString()
                ),
                params = params,
            )
            return response.isSuccess()
        } catch (e: Exception) {
            println(e)
            return false
        }
    }

    private fun getPrivateApiHeaders(privateApiInfo: PrivateApiInfo, data: String): Map<String, String> {
        return ByBitPrivateApiInfo(
            privateApiInfo = privateApiInfo,
            data = data,
            timestamp = Instant.now().toEpochMilli(),
            receiveWindow = 5000,
        ).getRequestHeaders()
    }
}

private fun Map<String, Any>.toQueryString(): String {
    return this.entries.stream()
        .map { (key, value) -> "$key=$value" }
        .reduce { p1, p2 -> "$p1&$p2" }
        .orElse("")
}