package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import helpers.BaseDisabledTest
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import kotlin.test.assertEquals

class LsSecGetAccessTokenTest : BaseDisabledTest {
    @DisabledIf("hasNotLsSecApiInfo")
    @Test
    fun `엑세스 토큰 발급하기`() = runBlocking {
        val api = com.newy.algotrade.auth.adpter.out.external_system.LsSecAccessTokenHttpApi(
            DefaultHttpApiClient(
                OkHttpClient(),
                TestEnv.LsSec.url,
                JsonConverterByJackson(jacksonObjectMapper())
            )
        )

        val accessToken = api.findAccessToken(
            PrivateApiInfo(
                key = TestEnv.LsSec.apiKey,
                secret = TestEnv.LsSec.apiSecret,
            )
        )

        assert(accessToken.isNotEmpty())
        assertEquals(380, accessToken.length)
    }
}