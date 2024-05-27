package com.newy.algotrade.coroutine_based_application.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetCandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetUserStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetUserStrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnUserStrategySignalPort
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RunUserStrategyService(
    private val candlePort: GetCandlePort,
    private val userStrategyPort: GetUserStrategyPort,
    private val userStrategySignalHistoryPort: GetUserStrategySignalHistoryPort,
    private val userStrategySignalPort: OnUserStrategySignalPort,
) : RunUserStrategyUseCase {
    override fun run(productPriceKey: ProductPriceKey) {
        val candles = candlePort.getCandles(productPriceKey).takeIf { it.size > 0 } ?: return

        userStrategyPort.filterBy(productPriceKey).forEach { (userStrategyKey, strategy) ->
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