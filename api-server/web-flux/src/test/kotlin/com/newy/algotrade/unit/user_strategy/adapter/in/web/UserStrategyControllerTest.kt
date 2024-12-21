package com.newy.algotrade.unit.user_strategy.adapter.`in`.web

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.consts.ProductCategory
import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.web_flux.user_strategy.adapter.`in`.web.model.SetUserStrategyRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserStrategyControllerTest {
    @Test
    fun `requestModel 을 incomingPortModel 로 변경하기`() = runBlocking {
        var incomingPortModel: SetUserStrategyCommand? = null
        val controller = SetUserStrategyController(
            setUserStrategyUseCase = { strategy ->
                1.toLong().also {
                    incomingPortModel = strategy
                }
            }
        )

        controller.setMarketAccount(
            SetUserStrategyRequest(
                marketAccountId = 1,
                strategyClassName = "BuyTripleRSIStrategy",
                productCategory = "USER_PICK",
                productType = "SPOT",
                productCodes = listOf("BTCUSDT", "ETHUSDT"),
                timeFrame = "M1",
            )
        )

        Assertions.assertEquals(
            SetUserStrategyCommand(
                marketAccountId = 1,
                strategyClassName = "BuyTripleRSIStrategy",
                productCategory = ProductCategory.USER_PICK,
                productType = ProductType.SPOT,
                productCodes = listOf("BTCUSDT", "ETHUSDT"),
                timeFrame = Candle.TimeFrame.M1,
            ),
            incomingPortModel
        )
    }
}