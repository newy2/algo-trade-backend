package com.newy.algotrade.unit.user_strategy.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.SetUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SetUserStrategyControllerTest : SetUserStrategyUseCase {
    private var domainModel: SetUserStrategyCommand? = null

    @Test
    fun `request 모델을 command 모델로 변경하기`() = runBlocking {
        val controller = SetUserStrategyController(this@SetUserStrategyControllerTest)
        controller.setMarketAccount(
            SetUserStrategyRequest(
                marketAccountId = 1,
                strategyClassName = "BuyTripleRSIStrategy",
                productCategory = "USER_PICK",
                productType = "SPOT",
                productCodes = listOf("BTC", "ETH"),
                timeFrame = "M1",
            )
        )

        Assertions.assertEquals(
            SetUserStrategyCommand(
                marketAccountId = 1,
                strategyClassName = "BuyTripleRSIStrategy",
                productCategory = ProductCategory.USER_PICK,
                productType = ProductType.SPOT,
                productCodes = listOf("BTC", "ETH"),
                timeFrame = Candle.TimeFrame.M1,
            ),
            domainModel
        )
    }

    override suspend fun setUserStrategy(strategy: SetUserStrategyCommand): Boolean {
        domainModel = strategy
        return true
    }
}