package com.newy.algotrade.auth.adpter.out.external_system

import com.newy.algotrade.auth.port.out.AccessTokenPort
import com.newy.algotrade.common.web.http.FormData
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.common.web.http.post
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.auth.jackson.LsSecAccessTokenHttpResponse

class LsSecAccessTokenHttpApi(
    private val client: HttpApiClient,
) : AccessTokenPort<PrivateApiInfo> {
    override suspend fun findAccessToken(info: PrivateApiInfo): String =
        client.post<LsSecAccessTokenHttpResponse>(
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
