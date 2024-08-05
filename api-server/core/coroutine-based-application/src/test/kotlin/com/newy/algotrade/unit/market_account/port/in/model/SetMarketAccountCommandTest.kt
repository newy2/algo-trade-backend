package com.newy.algotrade.unit.market_account.port.`in`.model

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SetMarketAccountCommandTest {
    private val incomingPortModel = SetMarketAccountCommand(
        userId = 1,
        market = Market.LS_SEC,
        isProduction = true,
        displayName = "displayName",
        appKey = "appKey",
        appSecret = "appSecret",
    )

    @Test
    fun `userId 는 0 이상이어야 함`() {
        assertThrows<ConstraintViolationException> { incomingPortModel.copy(userId = -1) }
        assertThrows<ConstraintViolationException> { incomingPortModel.copy(userId = 0) }
        assertDoesNotThrow {
            incomingPortModel.copy(userId = 1)
            incomingPortModel.copy(userId = 2)
        }
    }

    @Test
    fun `displayName 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(displayName = "")
        }
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(displayName = " ")
        }
        assertDoesNotThrow {
            incomingPortModel.copy(displayName = "a")
        }
    }

    @Test
    fun `appKey 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(appKey = "")
        }
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(appKey = " ")
        }
        assertDoesNotThrow {
            incomingPortModel.copy(appKey = "a")
        }
    }

    @Test
    fun `appSecret 는 NotBlank 이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(appSecret = "")
        }
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(appSecret = " ")
        }
        assertDoesNotThrow {
            incomingPortModel.copy(appSecret = "a")
        }
    }

    @Test
    fun toDomainEntity() {
        assertEquals(
            MarketAccount(
                userId = 1,
                marketServer = MarketServer(
                    id = 2,
                    marketId = 3
                ),
                displayName = "displayName",
                appKey = "appKey",
                appSecret = "appSecret",
            ),
            incomingPortModel.toDomainEntity(
                MarketServer(
                    id = 2,
                    marketId = 3,
                )
            )
        )
    }
}