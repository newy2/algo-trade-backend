package com.newy.algotrade.common.web.helper

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.auth.domain.jackson.LsSecAccessTokenHttpResponse
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.web.http.FormData
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.HttpApiRateLimit
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.market_account.adapter.out.external_system.model.LsSecGetWalletBalanceHttpResponse
import com.newy.algotrade.product.adapter.out.external_system.model.LsSecProductHttpResponse
import com.newy.algotrade.product.domain.RegisterProducts

class LsSecApiHelper private constructor(
    private val client: HttpApiClient,
) : ApiHelper() {
    companion object {
        private var INSTANCE: LsSecApiHelper? = null
        fun getInstance(client: HttpApiClient): LsSecApiHelper {
            return INSTANCE ?: LsSecApiHelper(client).apply {
                INSTANCE = this
            }
        }

        private val AUTHORIZATION = object {
            val PATH = "/oauth2/token"
            val TR_CODE = object {
                val GET_ACCESS_TOKEN = "token"
            }
        }
        private val ACCOUNT = object {
            val PATH = "/stock/accno"
            val TR_CODE = object {
                val GET_WALLET_BALANCE = "t0424"
            }
        }
        private val ETC = object {
            val PATH = "/stock/etc"
            val TR_CODE = object {
                val GET_PRODUCTS = "t8436"
            }
        }
    }

    init {
        rateLimits.put(AUTHORIZATION.TR_CODE.GET_ACCESS_TOKEN, HttpApiRateLimit(delayMillis = 0))
        rateLimits.put(ACCOUNT.TR_CODE.GET_WALLET_BALANCE, HttpApiRateLimit(delayMillis = 1000))
        rateLimits.put(ETC.TR_CODE.GET_PRODUCTS, HttpApiRateLimit(delayMillis = 500))
    }

    suspend fun getAccessToken(privateApiInfo: PrivateApiInfo): String {
        awaitRateLimit(AUTHORIZATION.TR_CODE.GET_ACCESS_TOKEN)

        try {
            return client.post<LsSecAccessTokenHttpResponse>(
                path = AUTHORIZATION.PATH,
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                params = mapOf(
                    "grant_type" to "client_credentials",
                    "scope" to "oob",
                    "appkey" to privateApiInfo.appKey,
                    "appsecretkey" to privateApiInfo.appSecret,
                ),
                body = FormData()
            ).accessToken
        } catch (e: Exception) {
            println(e)
            return ""
        }
    }

    override suspend fun isValidPrivateApiInfo(privateApiInfo: PrivateApiInfo): Boolean {
        val trCode = ACCOUNT.TR_CODE.GET_WALLET_BALANCE
        awaitRateLimit(trCode)

        val response = client.post<LsSecGetWalletBalanceHttpResponse>(
            path = ACCOUNT.PATH,
            headers = getPrivateApiHeaders(
                privateApiInfo = privateApiInfo,
                trCode = trCode,
            )
        )

        return response.isSuccess()
    }

    override suspend fun getProducts(privateApiInfo: PrivateApiInfo?, productType: ProductType): RegisterProducts {
        val trCode = ETC.TR_CODE.GET_PRODUCTS
        awaitRateLimit(trCode)

        val response = client.post<LsSecProductHttpResponse>(
            path = ETC.PATH,
            headers = getPrivateApiHeaders(
                privateApiInfo = privateApiInfo!!,
                trCode = trCode,
            ),
            body = mapOf(
                "${trCode}InBlock" to mapOf(
                    "gubun" to "0",
                )
            ),
        )

        return RegisterProducts(response.products.filter { it.isStock() }.map { it.toDomainModel() })
    }

    private suspend fun getPrivateApiHeaders(privateApiInfo: PrivateApiInfo, trCode: String) = mapOf(
        "Content-Type" to "application/json; charset=utf-8",
        "authorization" to "Bearer ${getAccessToken(privateApiInfo)}",
        "tr_cd" to trCode,
        "tr_cont" to "N"
    )
}