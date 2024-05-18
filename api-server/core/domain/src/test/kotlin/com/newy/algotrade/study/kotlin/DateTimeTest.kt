package com.newy.algotrade.study.kotlin

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@DisplayName("시간 관련 클래스 사용 방법")
class DateTimeTest {
    @Test
    fun `OffsetDateTime - 생성`() {
        val localDateTime = LocalDateTime.now()

        arrayOf(
            OffsetDateTime.of(localDateTime, ZoneOffset.of("Z")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+0")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+00")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+0000")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+00:00")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+00:00:00")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("-0")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("-00")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("-0000")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("-00:00")),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("-00:00:00")),
        ).forEach {
            assertEquals(OffsetDateTime.of(localDateTime, ZoneOffset.UTC), it)
        }

        assertNotEquals(
            OffsetDateTime.of(localDateTime, ZoneOffset.UTC),
            OffsetDateTime.of(localDateTime, ZoneOffset.of("+00:00:01")),
        )
    }

    @Test
    fun `OffsetDateTime - toString`() {
        assertEquals(
            "2024-05-01T09:01:01Z",
            OffsetDateTime.of(LocalDateTime.parse("2024-05-01T09:01:01"), ZoneOffset.UTC).toString()
        )
        assertEquals(
            "2024-05-01T09:01:02+00:00:05",
            OffsetDateTime.of(LocalDateTime.parse("2024-05-01T09:01:02"), ZoneOffset.of("+00:00:05")).toString(),
        )
        assertEquals(
            "2024-05-01T09:01+00:05",
            OffsetDateTime.of(LocalDateTime.parse("2024-05-01T09:01:00"), ZoneOffset.of("+00:05:00")).toString(),
            "시간값과 시간대가 0초인 경우, 초단위 생략 됨"
        )
        assertEquals(
            "2024-05-01T09:00+05:00",
            OffsetDateTime.of(LocalDateTime.parse("2024-05-01T09:00:00"), ZoneOffset.of("+05:00:00")).toString(),
        )
    }

    @Test
    fun `OffsetDateTime - offset 변경하기`() {
        val baseTime = OffsetDateTime.of(LocalDateTime.parse("2024-05-01T09:00:00"), ZoneOffset.of("+09:00"))

        assertEquals("2024-05-01T09:00+09:00", baseTime.toString())
        baseTime.withOffsetSameLocal(ZoneOffset.ofHours(0)).let {
            assertEquals("2024-05-01T09:00Z", it.toString(), "시간대만 조정")
            assertFalse(baseTime.isEqual(it))
        }
        baseTime.withOffsetSameInstant(ZoneOffset.ofHours(0)).let {
            assertEquals("2024-05-01T00:00Z", it.toString(), "시간값/시간대 같이 조정")
            assertTrue(baseTime.isEqual(it), "시간값/시간대 같이 조정하므로, 의미적인 시간 값이 같음")
        }
    }

    @Test
    fun `ZonedDateTime - 서머타임 시작 테스트`() {
        val localDateTime = LocalDateTime.parse("2024-03-31T01:59")
        assertEquals("2024-03-31T01:59", localDateTime.toString(), "3월 마지막주 일요일 (01시 59분)")

        ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Rome")).let {
            assertEquals("2024-03-31T01:59+01:00[Europe/Rome]", it.toString(), "서머타임 시작 전")
            assertEquals("2024-03-31T03:00+02:00[Europe/Rome]", it.plusMinutes(1).toString(), "서머타임 시작 (시간값/시간대 같이 조정)")
        }

        OffsetDateTime.of(localDateTime, ZoneOffset.of("+01:00")).let {
            assertEquals("2024-03-31T01:59+01:00", it.toString(), "서머타임 시작 전")
            assertEquals("2024-03-31T02:00+01:00", it.plusMinutes(1).toString(), "서머타임 시작")
        }
    }

    @Test
    fun `ZonedDateTime - 서머타임 종료 테스트`() {
        val localDateTime = LocalDateTime.parse("2024-10-27T02:59")
        assertEquals("2024-10-27T02:59", localDateTime.toString(), "10월 마지막주 일요일 (02시 59분)")

        ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Rome")).let {
            assertEquals("2024-10-27T02:59+02:00[Europe/Rome]", it.toString(), "서머타임 종료 전")
            assertEquals("2024-10-27T02:00+01:00[Europe/Rome]", it.plusMinutes(1).toString(), "서머타임 종료 (시간값/시간대 같이 조정)")
        }

        OffsetDateTime.of(localDateTime, ZoneOffset.of("+02:00")).let {
            assertEquals("2024-10-27T02:59+02:00", it.toString(), "서머타임 종료 전")
            assertEquals("2024-10-27T03:00+02:00", it.plusMinutes(1).toString(), "서머타임 종료")
        }
    }
}

class DateTimeFormatterTest {
    @Test
    fun `문자열 시간 값을 OffsetDateTime 으로 파싱하기`() {
        val formatter = DateTimeFormatter
            .ofPattern("yyyyMMdd HHmmss")
            .withZone(ZoneOffset.ofHours(9))

        val offsetDateTime = OffsetDateTime.parse("20240501 090000", formatter)

        assertEquals("2024-05-01T09:00+09:00", offsetDateTime.toString())
    }
}

class DurationTest {
    @Test
    fun `Duration 객체를 Long 으로 변환하기`() {
        assertEquals(1, Duration.ofMinutes(1).toMinutes())
        assertEquals(60, Duration.ofHours(1).toMinutes())
        assertEquals(60 * 24, Duration.ofDays(1).toMinutes())
    }

    @Test
    fun `동등성 테스트`() {
        assertEquals(Duration.ofHours(1), Duration.ofMinutes(60))
        assertEquals(Duration.ofDays(1), Duration.ofHours(24))
    }
}