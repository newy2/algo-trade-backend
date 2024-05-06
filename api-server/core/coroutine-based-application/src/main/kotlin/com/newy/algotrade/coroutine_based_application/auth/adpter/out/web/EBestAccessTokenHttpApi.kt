package com.newy.algotrade.coroutine_based_application.auth.adpter.out.web

import com.newy.algotrade.coroutine_based_application.auth.port.out.GetAccessTokenPort
import com.newy.algotrade.coroutine_based_application.common.web.FormData
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.post
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.auth.adapter.out.web.model.jackson.EBestAccessTokenHttpResponse

class EBestAccessTokenHttpApi(
    private val client: HttpApiClient,
) : GetAccessTokenPort<PrivateApiInfo> {
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
