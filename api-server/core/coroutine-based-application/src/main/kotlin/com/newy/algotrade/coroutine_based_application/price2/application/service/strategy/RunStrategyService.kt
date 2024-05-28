package com.newy.algotrade.coroutine_based_application.price2.application.service.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetStrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnCreateStrategySignalPort
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RunStrategyService(
    private val candlePort: GetCandlePort,
    private val strategyPort: GetStrategyPort,
    private val strategySignalHistoryPort: GetStrategySignalHistoryPort,
    private val strategySignalPort: OnCreateStrategySignalPort,
) : RunStrategyUseCase {
    override fun runStrategy(productPriceKey: ProductPriceKey) {
        val candles = candlePort.getCandles(productPriceKey).takeIf { it.size > 0 } ?: return

        strategyPort.filterBy(productPriceKey).forEach { (userStrategyKey, strategy) ->
            val userStrategyId = userStrategyKey.userStrategyId
            val history = strategySignalHistoryPort.get(userStrategyId)

            strategy.shouldOperate(candles.lastIndex, history).let { orderType ->
                if (orderType == OrderType.NONE) {
                    return
                }

                strategySignalPort.onCreateSignal(
                    userStrategyId,
                    OrderSignal(orderType, candles.lastCandle.time, candles.lastCandle.price.close),
                )
            }
        }
    }
}