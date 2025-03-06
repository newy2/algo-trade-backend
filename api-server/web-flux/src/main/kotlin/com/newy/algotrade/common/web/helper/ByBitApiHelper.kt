package com.newy.algotrade.common.web.helper

import com.newy.algotrade.auth.domain.ByBitPrivateApiInfo
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.HttpApiRateLimit
import com.newy.algotrade.common.web.http.get
import com.newy.algotrade.market_account.adapter.out.external_system.model.ByBitGetWalletBalanceHttpResponse
import com.newy.algotrade.product.adapter.out.external_system.model.ByBitProductHttpResponse
import com.newy.algotrade.product.domain.RegisterProducts
import java.time.Instant

class ByBitApiHelper private constructor(
    private val client: HttpApiClient,
) : ApiHelper() {
    companion object {
        private var INSTANCE: ByBitApiHelper? = null
        fun getInstance(client: HttpApiClient): ByBitApiHelper {
            return INSTANCE ?: ByBitApiHelper(client).apply {
                INSTANCE = this
            }
        }

        private const val GET_WALLET_BALANCE_PATH = "/v5/account/wallet-balance"
        private const val GET_INSTRUMENTS_INFO = "/v5/market/instruments-info"
    }

    init {
        rateLimits.put(GET_WALLET_BALANCE_PATH, HttpApiRateLimit(delayMillis = 500))
        rateLimits.put(GET_INSTRUMENTS_INFO, HttpApiRateLimit(delayMillis = 500))
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

    override suspend fun getProducts(privateApiInfo: PrivateApiInfo?, productType: ProductType): RegisterProducts {
        awaitRateLimit(GET_INSTRUMENTS_INFO)

        val response = client.get<ByBitProductHttpResponse>(
            path = GET_INSTRUMENTS_INFO,
            params = mapOf(
                "category" to when (productType) {
                    ProductType.SPOT -> "spot"
                    ProductType.PERPETUAL_FUTURE -> "linear"
                },
                "limit" to "1000"
            ),
            jsonExtraValues = ByBitProductHttpResponse.jsonExtraValues(productType)
        )

        return RegisterProducts(response.products.map { it.toDomainModel() })
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