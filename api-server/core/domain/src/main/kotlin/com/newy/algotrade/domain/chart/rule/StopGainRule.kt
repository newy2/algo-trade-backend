package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.ClosePriceIndicator
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
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
                isBuyGainSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
            } else {
                isSellGainSatisfied(history.lastOrderSignal().orderPrice, currentPrice)
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