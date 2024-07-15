package com.newy.algotrade.unit.market_account.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.SetMarketAccountUseCase
import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.SetMarketAccountController
import com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model.SetMarketAccountRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SetMarketAccountControllerTest : SetMarketAccountUseCase {
    private var domainModel: SetMarketAccountCommand? = null

    @Test
    fun `request 모델을 command 모델로 변경하기`() = runBlocking {
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
                market = Market.LS_SEC,
                isProduction = true,
                displayName = "name",
                appKey = "key",
                appSecret = "secret",
            ),
            domainModel
        )
    }

    override suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean {
        domainModel = marketAccount
        return true
    }
}