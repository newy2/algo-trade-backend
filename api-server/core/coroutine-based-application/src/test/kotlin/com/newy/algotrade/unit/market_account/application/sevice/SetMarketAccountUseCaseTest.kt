package com.newy.algotrade.unit.market_account.application.sevice

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.HasMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.SetMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.service.SetMarketAccountService
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SetMarketAccountUseCaseTest : HasMarketAccountPort, SetMarketAccountPort {
    private val repository = mutableListOf<SetMarketAccountCommand>()

    @BeforeEach
    fun setUp() {
        repository.clear()
    }

    @Test
    fun `사용자 계정 등록하기`() = runTest {
        val service = SetMarketAccountService(
            this@SetMarketAccountUseCaseTest,
            this@SetMarketAccountUseCaseTest
        )
        val account = SetMarketAccountCommand(
            market = Market.BY_BIT,
            isProduction = false,
            displayName = "name",
            appKey = "key",
            appSecret = "secret"
        )

        assertTrue(service.setMarketAccount(account), "신규 사용자 계정을 등록한 경우")
    }

    @Test
    fun `이미 등록된 사용자 계정인 경우`() = runTest {
        val service = SetMarketAccountService(
            this@SetMarketAccountUseCaseTest,
            this@SetMarketAccountUseCaseTest
        )
        val account = SetMarketAccountCommand(
            market = Market.BY_BIT,
            isProduction = false,
            displayName = "name",
            appKey = "key",
            appSecret = "secret"
        )

        service.setMarketAccount(account)

        try {
            service.setMarketAccount(account)
        } catch (exception: DuplicateDataException) {
        }
    }

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        repository.add(marketAccount)
        return true
    }

    override suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        return repository.contains(marketAccount)
    }
}