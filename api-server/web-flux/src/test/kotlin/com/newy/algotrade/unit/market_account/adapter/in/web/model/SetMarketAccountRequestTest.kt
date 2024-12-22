package com.newy.algotrade.unit.market_account.adapter.`in`.web.model

import com.newy.algotrade.common.consts.GlobalEnv
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import com.newy.algotrade.market_account.port.`in`.model.SetMarketAccountCommand
import helpers.spring.TestEnv
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `market 은 Market enum 에 선언된 문자열만 입력 받을 수 있다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(market = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(market = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            Market.values().forEach {
                dto.copy(market = it.name)
            }
        }
    }

    @Test
    fun mapToIncomingPortModel() {
        GlobalEnv.initializeAdminUserId(TestEnv.TEST_ADMIN_USER_ID)

        assertEquals(
            SetMarketAccountCommand(
                userId = TestEnv.TEST_ADMIN_USER_ID,
                market = Market.LS_SEC,
                isProduction = true,
                displayName = "displayName",
                appKey = "key",
                appSecret = "secret",
            ),
            dto.toIncomingPortModel()
        )
    }
}