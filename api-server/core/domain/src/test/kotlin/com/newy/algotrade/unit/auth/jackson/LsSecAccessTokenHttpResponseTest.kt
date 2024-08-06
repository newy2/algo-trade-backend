package com.newy.algotrade.unit.auth.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.domain.auth.jackson.LsSecAccessTokenHttpResponse
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("토큰 발급 API Response DTO 단위테스트")
class LsSecAccessTokenHttpResponseTest {
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