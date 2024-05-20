package com.newy.algotrade.unit.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryUserStrategyStore
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.UserStrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import org.junit.jupiter.api.Test
import java.time.Duration

class RegisterUserStrategyUseCaseTest {
    // 등록하기
    @Test
    fun `등록하기`() {
        val store = InMemoryUserStrategyStore()
        val service = RegisterUserStrategyUseCase(
            InMemoryCandleStore(),
            store,
        )

        val key = UserStrategyKey(
            "user1",
            StrategyId.BuyTripleRSIStrategy,
            productPriceKey("BTCUSDT", Duration.ofMinutes(1))
        )

        service.register(key)
    }
    // 해제하기

}

class RegisterUserStrategyUseCase(
    private val candlePort: CandlePort,
    private val userStrategyPort: UserStrategyPort,
) {
    fun register(key: UserStrategyKey) {
        val candles = candlePort.getCandles(key.productPriceKey)
        val strategy = Strategy.create(key.strategyId, candles)
        userStrategyPort.add(key, strategy)
    }
}