package com.newy.algotrade.unit.user_strategy.adapter.`in`.web.model

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyRequest
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

val dto = SetUserStrategyRequest(
    marketAccountId = 1,
    strategyClassName = "BuyTripleRSIStrategy",
    productCategory = "USER_PICK",
    productType = "SPOT",
    productCodes = listOf("BTC"),
    timeFrame = "M1",
)

class SetUserStrategyRequestTest {
    @Test
    fun productCategory() {
        assertThrows<ConstraintViolationException> {
            dto.copy(productCategory = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productCategory = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            ProductCategory.values().forEach {
                dto.copy(productCategory = it.name)
            }
        }
    }

    @Test
    fun timeFrame() {
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(productType = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            ProductType.values().forEach {
                dto.copy(productType = it.name)
            }
        }
    }

    @Test
    fun productType() {
        assertThrows<ConstraintViolationException> {
            dto.copy(timeFrame = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(timeFrame = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            Candle.TimeFrame.values().forEach {
                dto.copy(timeFrame = it.name)
            }
        }
    }
}