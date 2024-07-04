package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.auth.adapter.out.web.model.jackson.LsSecAccessTokenHttpResponse
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("토큰 발급 API Response DTO 단위테스트")
class ResponseDtoTest {
    @Test
    fun `엑세스 토큰 발급 Response`() {
        val converter = JsonConverterByJackson(jacksonObjectMapper())

        val json = """
            {
                "access_token": "eyJ0eX...qM7OwQ",
                "scope": "oob",
                "token_type": "Bearer",
                "expires_in": 27531
            }
        """.trimIndent()

        converter.toObject<LsSecAccessTokenHttpResponse>(json).let {
            assertEquals("eyJ0eX...qM7OwQ", it.accessToken)
            assertEquals("Bearer", it.tokenType)
        }

    }
}

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