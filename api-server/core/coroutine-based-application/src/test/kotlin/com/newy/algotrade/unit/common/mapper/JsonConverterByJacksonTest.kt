package com.newy.algotrade.unit.common.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.common.mapper.toObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

data class SimpleData(val key: Int, val value: String)
class JsonConverterByJacksonTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun toJson() {
        val obj = SimpleData(key = 1, value = "a")
        val json = converter.toJson(obj)
        assertEquals("""{"key":1,"value":"a"}""", json)
    }

    @Test
    fun fromJson() {
        val json = """{"key":1,"value":"a"}"""
        val obj = converter.toObject<SimpleData>(json)
        assertEquals(SimpleData(key = 1, value = "a"), obj)
    }

    @Test
    fun `fromJson - Unit 타입으로 변환하기`() {
        assertEquals(Unit, converter.toObject<Unit>("OK"))
    }

    @Test
    fun `fromJson - String 타입으로 변환하기`() {
        assertEquals("OK", converter.toObject<String>("OK"))
    }
}
