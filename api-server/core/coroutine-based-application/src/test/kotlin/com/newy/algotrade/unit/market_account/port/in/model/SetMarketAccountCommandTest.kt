package com.newy.algotrade.unit.market_account.port.`in`.model

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.Market
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

val dto = SetMarketAccountCommand(
    market = Market.LS_SEC,
    isProduction = true,
    displayName = "displayName",
    appKey = "key",
    appSecret = "secret",
)

class SetMarketAccountCommandTest {
    @Test
    fun `displayName 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(displayName = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(displayName = " ")
        }
        assertDoesNotThrow {
            dto.copy(displayName = "a")
        }
    }

    @Test
    fun `appKey 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(appKey = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(appKey = " ")
        }
        assertDoesNotThrow {
            dto.copy(appKey = "a")
        }
    }

    @Test
    fun `appSecret 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(appSecret = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(appSecret = " ")
        }
        assertDoesNotThrow {
            dto.copy(appSecret = "a")
        }
    }
}