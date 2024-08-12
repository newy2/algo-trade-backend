package com.newy.algotrade.unit.market_account.sevice

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.port.out.ExistsMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.port.out.FindMarketServerPort
import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.service.MarketAccountCommandService
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.fail

private val incomingPortModel = SetMarketAccountCommand(
    userId = 1,
    market = Market.BY_BIT,
    isProduction = false,
    displayName = "name",
    appKey = "key",
    appSecret = "secret"
)

@DisplayName("예외 사항 테스트")
class MarketAccountCommandServiceExceptionTest {
    @Test
    fun `MarketServer 를 찾을 수 없는 경우`() = runTest {
        val notFoundMarketServerAdapter = FindMarketServerPort { _, _ -> null }
        val service = MarketAccountCommandService(
            existsMarketAccountPort = NoErrorMarketAccountAdapter(),
            saveMarketAccountPort = NoErrorMarketAccountAdapter(),
            findMarketServerPort = notFoundMarketServerAdapter,
        )

        try {
            service.setMarketAccount(incomingPortModel)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals(
                "market_server 를 찾을 수 없습니다 (market: ${incomingPortModel.market}, isProduction: ${incomingPortModel.isProduction})",
                e.message
            )
        }
    }

    @Test
    fun `중복된 MarketAccount 를 등록하려는 경우`() = runTest {
        val alreadySavedMarketAccountAdapter = ExistsMarketAccountPort { _ -> true }
        val service = MarketAccountCommandService(
            saveMarketAccountPort = NoErrorMarketAccountAdapter(),
            findMarketServerPort = NoErrorMarketAccountAdapter(),
            existsMarketAccountPort = alreadySavedMarketAccountAdapter,
        )

        try {
            service.setMarketAccount(incomingPortModel)
            fail()
        } catch (e: DuplicateDataException) {
            assertEquals("이미 등록된 appKey, appSecret 입니다.", e.message)
        }
    }
}

@DisplayName("해피패스 테스트")
class MarketAccountCommandServiceTest {
    @Test
    fun `문제 없이 실행되면, SaveMarketAccountPort#saveMarketAccount 의 결과값을 리던한다`() = runTest {
        val expected = MarketAccount(
            id = 10,
            userId = 1,
            marketServer = MarketServer(
                id = 1,
                marketId = 2,
            ),
            displayName = "displayName",
            appKey = "appKey",
            appSecret = "appSecret",
        )
        val service = MarketAccountCommandService(
            findMarketServerPort = NoErrorMarketAccountAdapter(),
            existsMarketAccountPort = NoErrorMarketAccountAdapter(),
            saveMarketAccountPort = { (_) -> expected },
        )

        assertEquals(expected, service.setMarketAccount(incomingPortModel))
    }
}

open class NoErrorMarketAccountAdapter : MarketAccountPort {
    override suspend fun existsMarketAccount(domainEntity: MarketAccount) = false
    override suspend fun saveMarketAccount(domainEntity: MarketAccount) = MarketAccount(
        id = 10,
        userId = 1,
        marketServer = MarketServer(
            id = 1,
            marketId = 2,
        ),
        displayName = "displayName",
        appKey = "appKey",
        appSecret = "appSecret",
    )

    override suspend fun findMarketServer(market: Market, isProductionServer: Boolean): MarketServer? =
        MarketServer(
            id = 1,
            marketId = 2,
        )
}