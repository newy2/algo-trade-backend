package com.newy.algotrade.unit.run_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RemoveStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import helpers.productPriceKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration

private val userStrategyKey = UserStrategyKey(
    "user1",
    "BuyTripleRSIStrategy",
    productPriceKey("BTCUSDT", Duration.ofMinutes(1))
)

@DisplayName("port 호출 순서 확인")
class RemoveStrategyUseCaseTest : NoErrorStrategyAdapter {
    private val methodCallLogs: MutableList<String> = mutableListOf()

    override fun removeStrategy(key: UserStrategyKey) {
        methodCallLogs.add("removeStrategy")
    }

    @Test
    fun `port 호출 순서 확인`() {
        val service = RemoveStrategyUseCase(this)
        service.removeStrategy(userStrategyKey)
        assertEquals(listOf("removeStrategy"), methodCallLogs)
    }
}

interface NoErrorStrategyAdapter : StrategyCommandPort {
    override fun addStrategy(key: UserStrategyKey, strategy: Strategy) {
        TODO("Not yet implemented")
    }
}