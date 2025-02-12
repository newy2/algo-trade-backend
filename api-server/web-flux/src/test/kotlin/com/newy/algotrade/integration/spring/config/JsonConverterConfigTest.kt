package com.newy.algotrade.integration.spring.config

import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.mapper.toObject
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals

class JsonConverterConfigTest(
    @Autowired private val jsonConverter: JsonConverter
) : BaseDataR2dbcTest() {
    data class DateTimeDto(val date: LocalDateTime)

    @Test
    fun `JsonConverter 는 LocalDateTime 을 JSON 으로 파싱할 수 있어야 한다`() {
        assertEquals(
            """{"date":"2024-05-01T09:01:01.123456"}""",
            jsonConverter.toJson(DateTimeDto(date = LocalDateTime.parse("2024-05-01T09:01:01.123456")))
        )
    }

    @Test
    fun `JsonConverter 는 JSON 을 LocalDateTime 으로 파싱할 수 있어야 한다`() {
        assertEquals(
            DateTimeDto(date = LocalDateTime.parse("2024-05-01T09:01:01.123456")),
            jsonConverter.toObject("""{"date":"2024-05-01T09:01:01.123456"}""")
        )
    }
}