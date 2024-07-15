package com.newy.algotrade.unit.user_strategy.adapter.`in`.web.model

import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyRequest
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


val dto = SetUserStrategyRequest(
    marketAccountId = 1,
    strategyId = 1,
    productCategory = "USER_PICK",
    productType = "SPOT",
    productCodes = listOf("BTC"),
)

@Disabled
class SetStrategyRequestTest {
    @Test
    fun marketAccountId() {
        assertThrows<ConstraintViolationException> { dto.copy(marketAccountId = -1) }
        assertThrows<ConstraintViolationException> { dto.copy(marketAccountId = 0) }
        assertDoesNotThrow {
            dto.copy(marketAccountId = 1)
            dto.copy(marketAccountId = 2)
        }
    }

    @Test
    fun strategyId() {
        assertThrows<ConstraintViolationException> { dto.copy(strategyId = -1) }
        assertThrows<ConstraintViolationException> { dto.copy(strategyId = 0) }
        assertDoesNotThrow {
            dto.copy(strategyId = 1)
            dto.copy(strategyId = 2)
        }
    }

    @Test
    fun productCategory() {
        assertThrows<ConstraintViolationException> {
            dto.copy(productCategory = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productCategory = "NOT_REGISTERED_NAME")
        }
        assertDoesNotThrow {
            dto.copy(productCategory = "USER_PICK")
            dto.copy(productCategory = "TOP_TRADING_VALUE", productCodes = emptyList())
        }
    }

    @Test
    fun productType() {
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "NOT_REGISTERED_NAME")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "SPOT_MARGIN")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "FUTURE")
        }
        assertDoesNotThrow {
            dto.copy(productType = "SPOT")
            dto.copy(productType = "PERPETUAL_FUTURE")
        }
    }

    @Test
    fun productCodes() {
        assertThrows<ConstraintViolationException> {
            dto.copy(productCodes = listOf(""))
            dto.copy(productCodes = listOf(" ", ""))
        }
    }

    @Test
    fun `productCategory 값이 'USER_PICK' 인 경우, productCodes 는 1개 이상이여야 한다`() {
        assertThrows<IllegalArgumentException> {
            dto.copy(
                productCategory = "USER_PICK",
                productCodes = emptyList()
            )
        }
        assertDoesNotThrow {
            dto.copy(
                productCategory = "USER_PICK",
                productCodes = listOf("BTC")
            )
        }
    }

    @Test
    fun `productCategory 값이 'TOP_TRADING_VALUE' 인 경우, productCodes 는 emptyList 여야 한다`() {
        assertThrows<IllegalArgumentException> {
            dto.copy(
                productCategory = "TOP_TRADING_VALUE",
                productCodes = listOf("BTC")
            )
        }
        assertDoesNotThrow {
            dto.copy(
                productCategory = "TOP_TRADING_VALUE",
                productCodes = emptyList()
            )
        }
    }
}