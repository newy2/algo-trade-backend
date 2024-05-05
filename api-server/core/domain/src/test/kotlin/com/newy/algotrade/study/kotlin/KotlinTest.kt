package com.newy.algotrade.study.kotlin

import com.newy.algotrade.unit.libs.helper.RESOURCE_CONTENT
import com.newy.algotrade.unit.libs.helper.RESOURCE_PATH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.test.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class KotlinTest {
    @Test
    fun `Duration 팩토리 메소드는 객체를 캐싱하지 않는다`() {
        Assertions.assertNotSame(Duration.ofMinutes(1), Duration.ofMinutes(1))
        Assertions.assertNotSame(Duration.ofMinutes(1), 1.minutes.toJavaDuration())
    }

    @Test
    fun `BigDecimal 팩토리 메소드는 객체를 캐싱한다`() {
        assertSame(BigDecimal.valueOf(0), BigDecimal.valueOf(0))
        assertSame(BigDecimal.valueOf(0), BigDecimal.ZERO)
        assertSame(BigDecimal.valueOf(0), 0.toBigDecimal())
    }

    @Test
    fun `string 을 list 로 변환하는 방법`() {
        val multiLineText = """
            abc
            def
        """.trimIndent()

        assertEquals(listOf("abc", "def"), multiLineText.split("\n"))
    }

    @Test
    fun `list 를 array 로 변환하는 방법`() {
        val list = listOf("abc", "def")

        Assertions.assertArrayEquals(arrayOf("abc", "def"), list.toTypedArray<String>())
        Assertions.assertArrayEquals(
            arrayOf("abc", "def"),
            list.stream().toArray<String> { length -> arrayOfNulls(length) })
    }
}

@DisplayName("resource 폴더의 파일을 읽어오는 방법")
class AccessResourceFolderTest {
    @Test
    fun `리소스 파일 경로는 역슬러시로 시작해야 함`() {
        val correctPath = "/csv/for-unit-test-file.csv"
        val notCorrectPath = "csv/for-unit-test-file.csv"

        correctPath.also {
            Assertions.assertNotNull(javaClass.getResource(it))
            Assertions.assertNotNull(javaClass.getResourceAsStream(it))
        }
        notCorrectPath.also {
            assertNull(javaClass.getResource(it))
            assertNull(javaClass.getResourceAsStream(it))
        }
    }

    @Test
    fun `javaClass#getResource 함수로 읽어오는 방법`() {
        val fromUrl = javaClass.getResource(RESOURCE_PATH)!!.readText()

        assertEquals(RESOURCE_CONTENT, fromUrl)
    }

    @Test
    fun `javaClass#getResourceAsStream 함수로 읽어오는 방법`() {
        val fromStream = javaClass.getResourceAsStream(RESOURCE_PATH)!!
            .bufferedReader()
            .lines()
            .collect(Collectors.joining("\n"))

        assertEquals(RESOURCE_CONTENT, fromStream)
    }
}

@DisplayName("환경 변수 읽어오는 방법")
class GetEnvironmentVariablesTest {
    @Test
    fun `환경 변수 읽는 방법`() {
        assertTrue(System.getenv("JAVA_HOME").isNotEmpty())
    }
}

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

@DisplayName("확장 클래스 테스트")
class ExtendsClassTest {
    interface BaseInterface {
        fun function(value: String = "abc"): String
    }

    @Test
    fun `인터페이스의 메소드 기본 파라미터는 상속된다`() {
        class ExtendsClass : BaseInterface {
            override fun function(value: String): String =
                value
        }

        val defaultParameter = ExtendsClass().function()
        assertEquals("abc", defaultParameter)
    }

    @Test
    fun `클래스의 메소드 기본 파라미터는 상속된다`() {
        open class BaseClass {
            open fun function(value: String = "abc"): String =
                "123"
        }

        class ExtendsClass : BaseClass() {
            override fun function(value: String): String =
                value
        }

        val defaultParameter = ExtendsClass().function()
        assertEquals("abc", defaultParameter)
    }

    @Test
    fun `추상 클래스의 메소드 기본 파라미터는 상속된다`() {
        abstract class BaseClass {
            abstract fun function(value: String = "abc"): String
        }

        class ExtendsClass : BaseClass() {
            override fun function(value: String): String =
                value
        }

        val defaultParameter = ExtendsClass().function()
        assertEquals("abc", defaultParameter)
    }
}

@DisplayName("메세지 암호화 모듈 테스트")
class MacTest {
    @Test
    fun `HexFormat_of 캐쉬 여부 확인`() {
        assertSame(HexFormat.of(), HexFormat.of())
    }

    @Test
    fun `bytearray 를 16진수 문자열로 변환하기`() {
        assertEquals("01", HexFormat.of().formatHex(byteArrayOf(1)))
        assertEquals("7f", HexFormat.of().formatHex(byteArrayOf(127)))
        assertEquals("80", HexFormat.of().formatHex(byteArrayOf(-128)))
        assertEquals("0102", HexFormat.of().formatHex(byteArrayOf(1, 2)))
    }

    @Test
    fun `Mac 사용법`() {
        val algorithm = "HmacSHA256"
        val secretKey = "secret"
        val message = "hello"

        val mac = Mac.getInstance(algorithm).also {
            it.init(SecretKeySpec(secretKey.toByteArray(), algorithm))
        }

        val bytes = mac.doFinal(message.toByteArray())

        assertEquals(
            "88aab3ede8d3adf94d26ab90d3bafd4a2083070c3bcce9c014ee04a443847c0b",
            HexFormat.of().formatHex(bytes)
        )
    }
}