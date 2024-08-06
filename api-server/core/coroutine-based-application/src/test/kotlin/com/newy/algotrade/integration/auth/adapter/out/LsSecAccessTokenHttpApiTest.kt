package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.domain.auth.PrivateApiInfo
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LsSecGetAccessTokenTest {
    @Test
    fun `엑세스 토큰 발급하기`() = runBlocking {
        val api = LsSecAccessTokenHttpApi(
            DefaultHttpApiClient(
                OkHttpClient(),
                TestEnv.LsSec.url,
                JsonConverterByJackson(jacksonObjectMapper())
            )
        )

        val accessToken = api.accessToken(
            PrivateApiInfo(
                key = TestEnv.LsSec.apiKey,
                secret = TestEnv.LsSec.apiSecret,
            )
        )

        assert(accessToken.isNotEmpty())
        assertEquals(380, accessToken.length)
    }
}