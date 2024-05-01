package com.newy.algotrade.study.libs

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("Jackson 역질렬화 테스트 (JSON -> Object)")
class JacksonDeserializeTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `DTO 에서 선언하지 않은 key 가 JSON 에 포함된 경우, 에러가 발생한다`() {
        val json = """
            {
                "known": "a",
                "unknown": 1
            }
        """.trimIndent()

        class DTO(val known: String)

        assertThrows<UnrecognizedPropertyException> {
            converter.toObject<DTO>(json)
        }
    }

    @Test
    fun `DTO 에서 선언하지 않은 key 를 무시하는 방법 1`() {
        val json = """
            {
                "known": "a",
                "unknown": 1
            }
        """.trimIndent()

        @JsonIgnoreProperties(ignoreUnknown = true)
        class DTO(val known: String)

        assertDoesNotThrow {
            converter.toObject<DTO>(json)
        }
    }

    @Test
    fun `DTO 에서 선언하지 않은 key 를 무시하는 방법 2`() {
        val json = """
            {
                "known": "a",
                "unknown": 1
            }
        """.trimIndent()

        @JsonIgnoreProperties(value = ["unknown"])
        class DTO(val known: String)

        assertDoesNotThrow {
            converter.toObject<DTO>(json)
        }
    }

    @Test
    fun `multi depth JSON 파싱하기 - 사용하지 않는 2depth 이상의 JSON key("result" 의 "key2") 는 자동으로 무시된다`() {
        val json = """
            {
                "result": {
                    "key1": "a",
                    "key2": "b"
                }
            }
        """.trimIndent()

        data class DTO(val data: String) {
            @JsonCreator
            constructor(@JsonProperty("result") node: JsonNode) : this(data = node["key1"].asText())
        }

        assertEquals(DTO("a"), converter.toObject<DTO>(json))
    }

    @Test
    fun `조금 더 현실적인 parsing 예제`() {
        val json = """
            {
                "retCode": 0,
                "result": {
                    "symbol": "BTCUSDT",
                    "list": [
                        ["1", "2"], 
                        ["3", "4"]
                    ]
                },
                "retExtInfo": {},
                "time": 1672025956592
            }
        """.trimIndent()

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class DTO(val symbol: String, val prices: List<List<Int>>) {
            @JsonCreator
            constructor(@JsonProperty("result") node: JsonNode) : this(
                symbol = node["symbol"].asText(),
                prices = node["list"].map { it.map { it.asInt() } },
            )
        }

        assertEquals(
            DTO(symbol = "BTCUSDT", prices = listOf(listOf(1, 2), listOf(3, 4))),
            converter.toObject<DTO>(json)
        )
    }
}