package com.newy.algotrade.study.kotlin

import com.newy.algotrade.unit.common.helper.RESOURCE_CONTENT
import com.newy.algotrade.unit.common.helper.RESOURCE_PATH
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.*
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
        assertNotSame(Duration.ofMinutes(1), Duration.ofMinutes(1))
        assertNotSame(Duration.ofMinutes(1), 1.minutes.toJavaDuration())
    }

    @Test
    fun `BigDecimal 팩토리 메소드는 객체를 캐싱한다`() {
        assertSame(BigDecimal.valueOf(0), BigDecimal.valueOf(0))
        assertSame(BigDecimal.valueOf(0), BigDecimal.ZERO)
        assertSame(BigDecimal.valueOf(0), 0.toBigDecimal())
    }

    @Test
    fun `BigDecimal 의 'plus operator(+)' 는 scale(소수점 자리수)을 변경한다`() {
        val hundred = 100.toBigDecimal()
        val decimal = 0.01.toBigDecimal()

        assertEquals(BigDecimal("1.00"), ((hundred + decimal) / hundred))
        assertEquals(BigDecimal("1.0001"), hundred.plus(decimal).divide(hundred))
    }

    @Test
    fun `string 을 list 로 변환하는 방법`() {
        val multiLineText = """
            abc
            def
        """.trimIndent()

        assertEquals(listOf("abc", "def"), multiLineText.split(System.lineSeparator()))
    }

    @Test
    fun `list 를 array 로 변환하는 방법`() {
        val list = listOf("abc", "def")

        assertArrayEquals(arrayOf("abc", "def"), list.toTypedArray<String>())
        assertArrayEquals(arrayOf("abc", "def"), list.stream().toArray<String> { length -> arrayOfNulls(length) })
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
            .collect(Collectors.joining(System.lineSeparator()))

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
    fun `확장 클래스 - 메쏘드 기본 파라미터는 상속된다`() {
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
    fun `추상 클래스 - 메쏘드 기본 파라미터는 상속된다`() {
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

class EnumTest {
    enum class Enum {
        HELLO;
    }

    @Test
    fun `name - enum 에서 자동으로 생성해주는 변수`() {
        assertEquals("HELLO", Enum.HELLO.name)
    }

    @Test
    fun `문자열로 Enum 객체 찾기`() {
        assertEquals(Enum.HELLO, Enum.valueOf("HELLO"))
    }

    @Test
    fun `등록되지 않은 문자열로 찾기`() {
        assertThrows<IllegalArgumentException> {
            Enum.valueOf("NOT_REGISTERED_VALUE")
        }
    }
}

class ListTest {
    @Test
    fun `chunk - 리스트 분할하기`() {
        listOf("a", "b").let {
            val list = it.chunked(2)

            assertEquals(1, list.size)
            assertEquals(listOf("a", "b"), list[0])
        }
        listOf("a", "b", "c").let {
            val list = it.chunked(2)

            assertEquals(2, list.size)
            assertEquals(listOf("a", "b"), list[0])
            assertEquals(listOf("c"), list[1])
        }
    }

    @Test
    fun `chunk - 홀수 리스트를 짝수로 분할한 경우`() {
        assertThrows<IndexOutOfBoundsException>("구조분해 할당을 하면 에러 발생") {
            listOf("a").chunked(2).forEach { (first, second) -> }
        }

        assertDoesNotThrow("구조분해 할당의 2번째 element 를 사용하지 않으면 에러 없음") {
            listOf("a").chunked(2).forEach { (first, _) -> }
        }

        assertDoesNotThrow("lastOrNull() 을 사용해서 null 체크 하는게 나을 듯") {
            listOf("a").chunked(2).forEach {
                val first = it.first()
                val last = it.lastOrNull()
            }
        }
    }
}
