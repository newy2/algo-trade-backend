package com.newy.algotrade.unit.market_account.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.SetMarketAccountController
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SetMarketAccountControllerTest : SetMarketAccountUseCase {
    private var incomingPortModel: SetMarketAccountCommand? = null

    @Test
    fun `request 모델을 command 모델로 변경하기`() = runBlocking {
        GlobalEnv.initializeAdminUserId(TestEnv.TEST_ADMIN_USER_ID)

        val controller = SetMarketAccountController(this@SetMarketAccountControllerTest)
        controller.setMarketAccount(
            SetMarketAccountRequest(
                market = "LS_SEC",
                isProduction = true,
                displayName = "name",
                appKey = "key",
                appSecret = "secret",
            )
        )

        assertEquals(
            SetMarketAccountCommand(
                userId = TestEnv.TEST_ADMIN_USER_ID,
                market = Market.LS_SEC,
                isProduction = true,
                displayName = "name",
                appKey = "key",
                appSecret = "secret",
            ),
            incomingPortModel
        )
    }

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        incomingPortModel = marketAccount
        return true
    }
}