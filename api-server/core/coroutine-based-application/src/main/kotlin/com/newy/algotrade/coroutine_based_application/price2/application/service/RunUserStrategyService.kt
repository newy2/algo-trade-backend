package com.newy.algotrade.coroutine_based_application.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetUserStrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnCreateUserStrategySignalPort
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RunUserStrategyService(
    private val candlePort: GetCandlePort,
    private val strategyPort: GetStrategyPort,
    private val userStrategySignalHistoryPort: GetUserStrategySignalHistoryPort,
    private val userStrategySignalPort: OnCreateUserStrategySignalPort,
) : RunUserStrategyUseCase {
    override fun run(productPriceKey: ProductPriceKey) {
        val candles = candlePort.getCandles(productPriceKey).takeIf { it.size > 0 } ?: return

        strategyPort.filterBy(productPriceKey).forEach { (userStrategyKey, strategy) ->
            val userStrategyId = userStrategyKey.userStrategyId
            val history = userStrategySignalHistoryPort.get(userStrategyId)

            strategy.shouldOperate(candles.lastIndex, history).let { orderType ->
                if (orderType == OrderType.NONE) {
                    return
                }

                userStrategySignalPort.onCreateSignal(
                    userStrategyId,
                    OrderSignal(orderType, candles.lastCandle.time, candles.lastCandle.price.close),
                )
            }
        }
    }
}