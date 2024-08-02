package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

open class RunStrategyCommandService(
    private val candlesQuery: CandlesQuery,
    private val strategyPort: StrategyQueryPort,
    private val strategySignalHistoryPort: StrategySignalHistoryPort,
    private val onCreatedStrategySignalPort: OnCreatedStrategySignalPort,
) : RunStrategyUseCase {
    override suspend fun runStrategy(productPriceKey: ProductPriceKey) {
        val candles = candlesQuery.getCandles(productPriceKey).takeIf { it.size > 0 } ?: return

        strategyPort.filterBy(productPriceKey).forEach { (userStrategyKey, strategy) ->
            val userStrategyId = userStrategyKey.userStrategyId

            // TODO transaction?
            val history = strategySignalHistoryPort.getHistory(userStrategyId)

            strategy.shouldOperate(candles.lastIndex, history).let { orderType ->
                if (orderType == OrderType.NONE) {
                    return
                }

                // TODO 확인: 이 부분이 다른 곳에서도 사용하려나?
                val signal = StrategySignal(orderType, candles.lastCandle.time, candles.lastCandle.price.close)
                strategySignalHistoryPort.addHistory(userStrategyId, signal)
                onCreatedStrategySignalPort.onCreatedSignal(userStrategyId, signal)
            }
        }
    }
}