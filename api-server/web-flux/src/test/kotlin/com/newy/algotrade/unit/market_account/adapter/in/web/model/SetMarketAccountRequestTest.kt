package com.newy.algotrade.unit.market_account.adapter.`in`.web.model

import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

val dto = SetMarketAccountRequest(
    market = "LS_SEC",
    isProduction = true,
    displayName = "displayName",
    appKey = "key",
    appSecret = "secret",
)

class SetMarketAccountRequestTest {
    @Test
    fun market() {
        assertThrows<ConstraintViolationException> {
            dto.copy(market = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(market = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            dto.copy(market = "BY_BIT")
        }
        assertDoesNotThrow {
            dto.copy(market = "LS_SEC")
        }
    }

    @Test
    fun displayName() {
        assertThrows<ConstraintViolationException> {
            dto.copy(displayName = "")
        }
    }

    @Test
    fun appKey() {
        assertThrows<ConstraintViolationException> {
            dto.copy(appKey = "")
        }
    }

    @Test
    fun appSecret() {
        assertThrows<ConstraintViolationException> {
            dto.copy(appSecret = "")
        }
    }
}