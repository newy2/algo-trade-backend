package com.newy.algotrade.coroutine_based_application.auth.ebest

import com.newy.algotrade.coroutine_based_application.auth.AccessTokenApi
import com.newy.algotrade.coroutine_based_application.common.web.FormData
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.post
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.auth.model.jackson.ebest.EBestAccessTokenHttpResponse

class EBestAccessTokenHttpApi(
    private val client: HttpApiClient,
) : AccessTokenApi<PrivateApiInfo> {
    override suspend fun accessToken(info: PrivateApiInfo): String =
        client.post<EBestAccessTokenHttpResponse>(
            path = "/oauth2/token",
            headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
            params = mapOf(
                "grant_type" to "client_credentials",
                "scope" to "oob",
                "appkey" to info.key,
                "appsecretkey" to info.secret,
            ),
            body = FormData()
        ).let {
            it.accessToken
        }
}
