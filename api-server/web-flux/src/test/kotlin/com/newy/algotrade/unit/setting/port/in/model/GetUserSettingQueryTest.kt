package com.newy.algotrade.unit.setting.port.`in`.model

import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GetUserSettingQueryTest {
    private val inPortModel = GetUserSettingQuery(
        userId = 1,
    )

    @Test
    fun `userId 는 0 이상이어야 한다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(userId = -1) }
        assertThrows<ConstraintViolationException> { inPortModel.copy(userId = 0) }
        assertDoesNotThrow {
            inPortModel.copy(userId = 1)
            inPortModel.copy(userId = 2)
        }
    }
}