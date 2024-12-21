package com.newy.algotrade.unit.user_strategy.adapter.`in`.web.model

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.consts.ProductCategory
import com.newy.algotrade.common.domain.consts.ProductType
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
    productCodes = listOf("BTCUSDT"),
    timeFrame = "M1",
)

class SetUserStrategyRequestTest {
    @Test
    fun `productCategory 는 ProductCategory enum 값만 입력받을 수 있다`() {
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
    fun `productType 은 ProductType enum 값만 입력받을 수 있다`() {
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
    fun `timeFrame 은 TimeFrame enum 값만 입력받을 수 있다`() {
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