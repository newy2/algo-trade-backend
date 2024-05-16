package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.ClosePriceIndicator
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import java.math.BigDecimal

class StopGainRule(
    private val closePrice: ClosePriceIndicator,
    private val gainPercentage: BigDecimal,
) : Rule {
    companion object {
        private val HUNDRED = BigDecimal.valueOf(100)
    }

    constructor(closePrice: ClosePriceIndicator, gainPercentage: Number) : this(
        closePrice,
        gainPercentage.toDouble().toBigDecimal()
    )

    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        return history?.let {
            println(index)
            println(closePrice[index])

            if (!it.isOpened()) {
                return false
            }

            val currentPrice = closePrice[index]
            if (currentPrice > 30000.0.toBigDecimal()) {
                println("currentPrice > 30000 : $currentPrice")
            }
            if (it.lastOrderType() == OrderType.BUY) {
                isBuyGainSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
            } else {
                isSellGainSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
            }
        } ?: false
    }

    private fun isBuyGainSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = (HUNDRED + gainPercentage) / (HUNDRED);
        val threshold = entryPrice * lossRatioThreshold
        return currentPrice >= threshold
    }

    private fun isSellGainSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = (HUNDRED - gainPercentage) / (HUNDRED);
        val threshold = entryPrice * lossRatioThreshold
        return currentPrice <= threshold
    }
}