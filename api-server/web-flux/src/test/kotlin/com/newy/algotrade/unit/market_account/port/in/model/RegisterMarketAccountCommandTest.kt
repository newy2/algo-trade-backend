package com.newy.algotrade.unit.market_account.port.`in`.model

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RegisterMarketAccountCommandTest {
    private val inPortModel = RegisterMarketAccountCommand(
        userId = 1,
        marketCode = "BY_BIT",
        displayName = "displayName",
        appKey = "appKey",
        appSecret = "appSecret",
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

    @Test
    fun `marketCode 는 'BY_BIT', 'LS_SEC' 만 지원한다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(marketCode = "BYBIT") }
        assertThrows<ConstraintViolationException> { inPortModel.copy(marketCode = "LSSEC") }
        assertDoesNotThrow {
            inPortModel.copy(marketCode = "BY_BIT")
            inPortModel.copy(marketCode = "LS_SEC")
        }
    }

    @Test
    fun `displayName 는 빈 문자열일 수 없다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(displayName = "") }
        assertThrows<ConstraintViolationException> { inPortModel.copy(displayName = " ") }
        assertDoesNotThrow { inPortModel.copy(displayName = "A") }
    }

    @Test
    fun `appKey 는 빈 문자열일 수 없다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(appKey = "") }
        assertThrows<ConstraintViolationException> { inPortModel.copy(appKey = " ") }
        assertDoesNotThrow { inPortModel.copy(appKey = "A") }
    }

    @Test
    fun `appSecret 는 빈 문자열일 수 없다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(appSecret = "") }
        assertThrows<ConstraintViolationException> { inPortModel.copy(appSecret = " ") }
        assertDoesNotThrow { inPortModel.copy(appSecret = "A") }
    }

    @Test
    fun `InPort 모델은 도메인 모델로 매핑할 수 있어야 한다`() {
        assertEquals(
            MarketAccount(
                userId = 1,
                displayName = "displayName",
                marketCode = MarketCode.BY_BIT,
                privateApiInfo = PrivateApiInfo(
                    appKey = "appKey",
                    appSecret = "appSecret"
                )
            ),
            inPortModel.toDomainModel()
        )
    }
}