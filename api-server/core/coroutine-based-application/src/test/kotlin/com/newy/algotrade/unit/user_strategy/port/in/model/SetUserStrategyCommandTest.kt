package com.newy.algotrade.unit.user_strategy.port.`in`.model

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


val dto = SetUserStrategyCommand(
    marketAccountId = 1,
    strategyId = 1,
    productCategory = ProductCategory.USER_PICK,
    productType = ProductType.SPOT,
    productCodes = listOf("BTC"),
)

class SetUserStrategyCommandTest {
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
        assertThrows<IllegalArgumentException> {
            dto.copy(productCategory = ProductCategory.valueOf("NOT_REGISTERED_NAME"))
        }
    }

    @Test
    fun productType() {
        assertThrows<IllegalArgumentException> {
            dto.copy(productType = ProductType.valueOf("NOT_REGISTERED_NAME"))
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
                productCategory = ProductCategory.USER_PICK,
                productCodes = emptyList()
            )
        }
        assertDoesNotThrow {
            dto.copy(
                productCategory = ProductCategory.USER_PICK,
                productCodes = listOf("BTC")
            )
        }
    }

    @Test
    fun `productCategory 값이 'TOP_TRADING_VALUE' 인 경우, productCodes 는 emptyList 여야 한다`() {
        assertThrows<IllegalArgumentException> {
            dto.copy(
                productCategory = ProductCategory.TOP_TRADING_VALUE,
                productCodes = listOf("BTC")
            )
        }
        assertDoesNotThrow {
            dto.copy(
                productCategory = ProductCategory.TOP_TRADING_VALUE,
                productCodes = emptyList()
            )
        }
    }
}