package com.newy.algotrade.unit.common.helper

import com.newy.algotrade.domain.common.helper.SelfValidating
import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SelfValidatingTest {
    @Test
    fun test() {
        data class SimpleDto(
            @field:NotBlank val text: String
        ) : SelfValidating() {
            init {
                validate()
            }
        }

        assertThrows<ConstraintViolationException> {
            SimpleDto("")
        }
        assertDoesNotThrow {
            SimpleDto("a")
            SimpleDto("1")
            SimpleDto("a1")
        }
    }
}