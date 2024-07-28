package com.newy.algotrade.unit.market_account.sevice

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.service.SetMarketAccountService
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.market_account.MarketServer
import com.newy.algotrade.domain.market_account.SetMarketAccount
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

@DisplayName("port 호출 순서 확인")
class MethodCallHistoryTest : NoErrorMarketAccountAdapter() {
    private val methodCallLogs: MutableList<String> = mutableListOf()

    override suspend fun saveMarketAccount(domainEntity: SetMarketAccount) =
        super.saveMarketAccount(domainEntity).also {
            methodCallLogs.add("saveMarketAccount")
        }

    override suspend fun hasMarketAccount(domainEntity: SetMarketAccount) =
        super.hasMarketAccount(domainEntity).also {
            methodCallLogs.add("hasMarketAccount")
        }

    override suspend fun getMarketServer(market: Market, isProductionServer: Boolean) =
        super.getMarketServer(market, isProductionServer).also {
            methodCallLogs.add("getMarketServer")
        }

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    fun `port 호출 순서 확인`() = runTest {
        val service = SetMarketAccountService(this@MethodCallHistoryTest)
        service.setMarketAccount(incomingPortModel)

        assertEquals(
            listOf(
                "getMarketServer",
                "hasMarketAccount",
                "setMarketAccount",
            ),
            methodCallLogs
        )
    }
}

@DisplayName("예외 사항 테스트")
class ExceptionTest {
    @Test
    fun `MarketServer 를 찾을 수 없는 경우`() = runTest {
        val notFoundMarketServerAdapter = object : NoErrorMarketAccountAdapter() {
            override suspend fun getMarketServer(market: Market, isProductionServer: Boolean): MarketServer? = null
        }
        val service = SetMarketAccountService(notFoundMarketServerAdapter)

        try {
            service.setMarketAccount(incomingPortModel)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("market_server 를 찾을 수 없습니다 (market: BY_BIT, isProduction: false)", e.message)
        }
    }

    @Test
    fun `중복된 MarketAccount 를 등록하려는 경우`() = runTest {
        val alreadySavedMarketAccountAdapter = object : NoErrorMarketAccountAdapter() {
            override suspend fun hasMarketAccount(domainEntity: SetMarketAccount): Boolean = true
        }
        val service = SetMarketAccountService(alreadySavedMarketAccountAdapter)

        try {
            service.setMarketAccount(incomingPortModel)
            fail()
        } catch (e: DuplicateDataException) {
            assertEquals("이미 등록된 appKey, appSecret 입니다.", e.message)
        }
    }
}

open class NoErrorMarketAccountAdapter : MarketAccountPort {
    override suspend fun hasMarketAccount(domainEntity: SetMarketAccount) = false
    override suspend fun saveMarketAccount(domainEntity: SetMarketAccount) = true
    override suspend fun getMarketServer(market: Market, isProductionServer: Boolean): MarketServer? =
        MarketServer(
            id = 1,
            marketId = 2,
        )
}