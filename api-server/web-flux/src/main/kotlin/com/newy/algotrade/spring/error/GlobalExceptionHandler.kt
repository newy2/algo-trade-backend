package com.newy.algotrade.spring.error

import com.newy.algotrade.common.exception.DuplicateDataException
import com.newy.algotrade.common.exception.ForbiddenException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.common.exception.VerificationCodeException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleException(exception: ConstraintViolationException) =
        HttpStatus.BAD_REQUEST.let { httpStatus ->
            ResponseEntity
                .status(httpStatus)
                .body(
                    ErrorResponse(
                        message = httpStatus.reasonPhrase,
                        errors = exception.constraintViolations.map {
                            FieldError(
                                field = it.propertyPath.toString(),
                                value = it.invalidValue.toString(),
                                reason = it.message
                            )
                        }
                    ))
        }

    @ExceptionHandler(DuplicateDataException::class)
    fun handleException(exception: DuplicateDataException) =
        defaultClientErrorMessage(exception)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(exception: IllegalArgumentException) =
        defaultClientErrorMessage(exception)

    @ExceptionHandler(NotFoundRowException::class)
    fun handleException(exception: NotFoundRowException) =
        defaultClientErrorMessage(exception)

    @ExceptionHandler(VerificationCodeException::class)
    fun handleException(exception: VerificationCodeException) =
        defaultClientErrorMessage(exception)

    @ExceptionHandler(ForbiddenException::class)
    fun handleException(exception: ForbiddenException) =
        defaultClientErrorMessage(exception)

    private fun defaultClientErrorMessage(exception: Exception) =
        HttpStatus.BAD_REQUEST.let { httpStatus ->
            ResponseEntity
                .status(httpStatus)
                .body(
                    ErrorResponse(
                        message = exception.message ?: httpStatus.reasonPhrase,
                    )
                )
        }
}