package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ClosePriceIndicator
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
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

    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        return history?.let {
            if (!it.isOpened()) {
                return false
            }

            val currentPrice = closePrice[index]
            if (it.lastOrderType() == OrderType.BUY) {
                isBuyGainSatisfied(history.lastStrategySignal().price, currentPrice)
            } else {
                isSellGainSatisfied(history.lastStrategySignal().price, currentPrice)
            }
        } ?: false
    }

    private fun isBuyGainSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = HUNDRED.plus(gainPercentage).divide(HUNDRED)
        val threshold = entryPrice.multiply(lossRatioThreshold)
        return currentPrice >= threshold
    }

    private fun isSellGainSatisfied(entryPrice: BigDecimal, currentPrice: BigDecimal): Boolean {
        val lossRatioThreshold = HUNDRED.minus(gainPercentage).divide(HUNDRED)
        val threshold = entryPrice.multiply(lossRatioThreshold)
        return currentPrice <= threshold
    }
}