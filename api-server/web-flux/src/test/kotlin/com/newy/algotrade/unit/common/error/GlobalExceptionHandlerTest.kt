package com.newy.algotrade.unit.common.error

import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.web_flux.common.error.ErrorResponse
import com.newy.algotrade.web_flux.common.error.FieldError
import com.newy.algotrade.web_flux.common.error.GlobalExceptionHandler
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Path
import jakarta.validation.metadata.ConstraintDescriptor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ConstraintViolationExceptionTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `FieldError 가 없는 경우`() {
        val exception = ConstraintViolationException(emptySet())
        val result = handler.handleException(exception)

        val expected = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = "Bad Request",
                    errors = emptyList()
                )
            )
        assertEquals(expected, result)
    }

    @Test
    fun `FieldError 가 1개 있는 경우`() {
        val exception = ConstraintViolationException(setOf(
            object : FakeConstraintViolation<String>() {
                override fun getPropertyPath(): Path {
                    return object : FakePath() {
                        override fun toString() = "fieldName"
                    }
                }

                override fun getInvalidValue() = "fieldValue"
                override fun getMessage() = "reason"
            }
        ))
        val result = handler.handleException(exception)

        val expected = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = "Bad Request",
                    errors = listOf(
                        FieldError(
                            field = "fieldName",
                            value = "fieldValue",
                            reason = "reason",
                        )
                    )
                )
            )
        assertEquals(expected, result)
    }
}

class DuplicateDataExceptionHandlerTest : ClientErrorHandlerTest() {
    @Test
    fun `에러 메세지 테스트`() {
        assertErrorMessage(
            "이미 등록된 데이터 입니다.",
            handler.handleException(DuplicateDataException("이미 등록된 데이터 입니다.")),
        )
        assertErrorMessage(
            "Data already exists",
            handler.handleException(DuplicateDataException()),
        )
    }
}

class IllegalArgumentHandlerTest : ClientErrorHandlerTest() {
    @Test
    fun `에러 메세지 테스트`() {
        assertErrorMessage(
            "잘못된 데이터 입니다.",
            handler.handleException(IllegalArgumentException("잘못된 데이터 입니다.")),
        )
        assertErrorMessage(
            "Bad Request",
            handler.handleException(IllegalArgumentException()),
        )
    }
}

class NotFoundRowExceptionHandlerTest : ClientErrorHandlerTest() {
    @Test
    fun `에러 메세지 테스트`() {
        assertErrorMessage(
            "데이터를 찾을 수 없습니다.",
            handler.handleException(NotFoundRowException("데이터를 찾을 수 없습니다.")),
        )
        assertErrorMessage(
            "Can not found row",
            handler.handleException(NotFoundRowException()),
        )
    }
}

open class ClientErrorHandlerTest {
    protected val handler = GlobalExceptionHandler()

    protected fun assertErrorMessage(expectedErrorMessage: String, actual: ResponseEntity<ErrorResponse>) {
        val expected = ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = expectedErrorMessage,
                    errors = emptyList()
                )
            )
        assertEquals(expected, actual)
    }
}

open class FakeConstraintViolation<T> : ConstraintViolation<T> {
    override fun getPropertyPath(): Path {
        TODO("Not yet implemented")
    }

    override fun getInvalidValue(): Any {
        TODO("Not yet implemented")
    }

    override fun getMessage(): String {
        TODO("Not yet implemented")
    }

    override fun getMessageTemplate(): String {
        TODO("Not yet implemented")
    }

    override fun getRootBean(): T {
        TODO("Not yet implemented")
    }

    override fun getRootBeanClass(): Class<T> {
        TODO("Not yet implemented")
    }

    override fun getLeafBean(): Any {
        TODO("Not yet implemented")
    }

    override fun getExecutableParameters(): Array<Any> {
        TODO("Not yet implemented")
    }

    override fun getExecutableReturnValue(): Any {
        TODO("Not yet implemented")
    }

    override fun getConstraintDescriptor(): ConstraintDescriptor<*> {
        TODO("Not yet implemented")
    }

    override fun <U : Any?> unwrap(type: Class<U>?): U {
        TODO("Not yet implemented")
    }
}

open class FakePath : Path {
    override fun iterator(): MutableIterator<Path.Node> {
        TODO("Not yet implemented")
    }
}