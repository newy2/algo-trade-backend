package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.ClosePriceIndicator
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import java.math.BigDecimal

class StopLossRule(
    private val closePrice: ClosePriceIndicator,
    private val lossPercentage: BigDecimal,
) : Rule {
    companion object {
        private val HUNDRED = BigDecimal.valueOf(100)
    }

    constructor(closePrice: ClosePriceIndicator, lossPercentage: Number) : this(
        closePrice,
        lossPercentage.toDouble().toBigDecimal()
    )

    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        return history?.let {
            if (!it.isOpened()) {
                return false
            }

            val currentPrice = closePrice[index]
            if (it.lastOrderType() == OrderType.BUY) {
                isBuyStopSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
            } else {
                isSellStopSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
            }
        } ?: false
    }

    private fun isBuyStopSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = HUNDRED.minus(lossPercentage).divide(HUNDRED);
        val threshold = entryPrice.multiply(lossRatioThreshold)
        return currentPrice <= threshold
    }

    private fun isSellStopSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = HUNDRED.plus(lossPercentage).divide(HUNDRED);
        val threshold = entryPrice.multiply(lossRatioThreshold)
        return currentPrice >= threshold
    }
}