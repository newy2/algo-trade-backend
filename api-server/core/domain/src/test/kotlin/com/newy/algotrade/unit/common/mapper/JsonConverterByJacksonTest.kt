package com.newy.algotrade.unit.common.mapper

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


data class SimpleData(val key: Int, val value: String)
class JsonConverterByJacksonTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `toJson - Object 를 JSON 으로 변환하기`() {
        val obj = SimpleData(key = 1, value = "a")
        val json = converter.toJson(obj)
        assertEquals("""{"key":1,"value":"a"}""", json)
    }

    @Test
    fun `toObject - JSON 을 Object 타입으로 변환하기`() {
        val json = """{"key":1,"value":"a"}"""
        val obj = converter.toObject<SimpleData>(json)
        assertEquals(SimpleData(key = 1, value = "a"), obj)
    }

    @Test
    fun `toObject - JSON 을 Unit 타입으로 변환하기`() {
        assertEquals(Unit, converter.toObject<Unit>("OK"))
    }

    @Test
    fun `toObject - JSON 을 String 타입으로 변환하기`() {
        assertEquals("OK", converter.toObject<String>("OK"))
    }

    @Test
    fun `toObject - JSON 데이터와 추가 데이터를 더해서 매핑하기`() {
        data class ExtraData(
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
        val extraValues = mapOf("extraValue" to "b2")

        assertEquals(
            ExtraData(
                key = 1,
                value = "a",
                extraValue = "b2",
            ),
            converter.toObject<ExtraData>(json, extraValues)
        )
    }
}
