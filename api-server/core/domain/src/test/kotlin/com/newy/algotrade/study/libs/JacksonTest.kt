package com.newy.algotrade.study.libs

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("Jackson 역질렬화 테스트 (JSON -> Object)")
class JacksonDeserializeTest {
    private val jackson = jacksonObjectMapper()

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
            jackson.readValue(json, DTO::class.java)
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
            jackson.readValue(json, DTO::class.java)
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
            jackson.readValue(json, DTO::class.java)
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

        assertEquals(DTO("a"), jackson.readValue(json, DTO::class.java))
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
            jackson.readValue(json, DTO::class.java)
        )
    }

    @Test
    fun `JacksonInject - 역직렬화 시, JSON 이외의 데이터를 주입받고 싶을 때 사용한다`() {
        data class ExtraDTO(
            val key: Int,
            val value: String,
            @JacksonInject("extraValue") val extraValue: String
        )

        val json = """
            {
                "key": 1,
                "value": "a"
            }
        """.trimIndent()
        val extraValues = InjectableValues.Std(mapOf("extraValue" to "b2"))

        assertEquals(
            ExtraDTO(
                key = 1,
                value = "a",
                extraValue = "b2",
            ),
            jackson
                .readerFor(ExtraDTO::class.java)
                .with(extraValues)
                .readValue(json)
        )
    }
}

@DisplayName("커스텀 Deserializer 테스트")
class DeserializerTest {
    private val jackson = jacksonObjectMapper()
    private val json = """{ "name": "abc" }""".trimIndent()

    @JsonDeserialize(using = CustomDeserializer::class)
    data class Data(val name: String)

    class CustomDeserializer : StdDeserializer<Data> {
        constructor() : this(null)
        constructor(vc: Class<*>?) : super(vc)

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Data {
            val root = p.codec.readTree<JsonNode>(p)

            val name = root["name"].asText()
            val code = ctxt.findInjectableValue("code", null, null)

            return Data("$name-$code")
        }
    }

    @Test
    fun `InjectValue 가 있는 경우`() {
        val extraValues = InjectableValues.Std(mapOf("code" to "t1000"))

        assertEquals(
            Data(
                name = "abc-t1000"
            ),
            jackson
                .readerFor(Data::class.java)
                .with(extraValues)
                .readValue(json)
        )
    }

    @Test
    fun `InjectValue 가 없는 경우`() {
        assertThrows<InvalidDefinitionException> {
            jackson
                .readerFor(Data::class.java)
                .readValue(json)
        }
    }

    @Test
    fun `InjectValue 를 못찾는 경우`() {
        val noExtraValues = InjectableValues.Std()

        assertThrows<NullPointerException> {
            jackson
                .readerFor(Data::class.java)
                .with(noExtraValues)
                .readValue(json)
        }
    }
}