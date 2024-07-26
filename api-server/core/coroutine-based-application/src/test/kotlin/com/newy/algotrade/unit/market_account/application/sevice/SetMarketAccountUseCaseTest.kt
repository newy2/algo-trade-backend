package com.newy.algotrade.unit.market_account.application.sevice

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.MarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.service.SetMarketAccountService
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.fail

open class NoErrorAdapter : MarketAccountPort {
    override suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean = false

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean = true

}

private val command = SetMarketAccountCommand(
    market = Market.BY_BIT,
    isProduction = false,
    displayName = "name",
    appKey = "key",
    appSecret = "secret"
)

@DisplayName("port 호출 순서 확인")
class SetMarketAccountServiceTest : NoErrorAdapter() {
    private var log: String = ""

    @Test
    fun `port 호출 순서 확인`() = runTest {
        val service = SetMarketAccountService(this@SetMarketAccountServiceTest)

        service.setMarketAccount(command)

        assertEquals("hasMarketAccount setMarketAccount ", log)
    }

    override suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        log += "hasMarketAccount "
        return super.hasMarketAccount(marketAccount)
    }

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        log += "setMarketAccount "
        return super.setMarketAccount(marketAccount)
    }
}

@DisplayName("예외 사항 테스트")
class SetMarketAccountServiceExceptionTest {
    @Test
    fun `이미 등록한 market account 경우`() = runTest {
        val alreadySavedAdapter = object : NoErrorAdapter() {
            override suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean = true
        }

        val service = SetMarketAccountService(alreadySavedAdapter)

        try {
            service.setMarketAccount(command)
            fail()
        } catch (e: DuplicateDataException) {
            assertEquals("이미 등록된 appKey, appSecret 입니다.", e.message)
        }
    }
}