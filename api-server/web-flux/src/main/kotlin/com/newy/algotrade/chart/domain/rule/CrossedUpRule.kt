package com.newy.algotrade.chart.domain.rule

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ConstDecimalIndicator
import com.newy.algotrade.chart.domain.indicator.Indicator
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import com.newy.algotrade.common.extension.compare

open class CrossedUpRule(private val upper: Indicator, private val lower: Indicator) : Rule {
    constructor(upper: Indicator, threshold: Number) : this(upper, ConstDecimalIndicator(threshold))

    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean {
        if (index == 0 || upper[index] <= lower[index]) {
            return false
        }

        val beforeIndex = findBeforeIndex(currentIndex = index)
        return upper[beforeIndex] < lower[beforeIndex]
    }

    private fun findBeforeIndex(currentIndex: Int): Int {
        var result = currentIndex

        do {
            result--
        } while (result > 0 && upper[result].compare(lower[result]))

        return result
    }
}