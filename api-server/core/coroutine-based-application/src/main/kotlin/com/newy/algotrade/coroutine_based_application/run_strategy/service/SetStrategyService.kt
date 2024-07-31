package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.domain.chart.strategy.Strategy

open class SetStrategyService(
    private val candlePort: CandleQueryPort, // TODO service 로 변경하기
    private val strategyPort: StrategyCommandPort,
) : SetStrategyUseCase {
    override fun setStrategy(key: UserStrategyKey) {
        val candles = candlePort.getCandles(key.productPriceKey)
        val strategy = Strategy.fromClassName(key.strategyClassName, candles)
        strategyPort.addStrategy(key, strategy)
    }
}
