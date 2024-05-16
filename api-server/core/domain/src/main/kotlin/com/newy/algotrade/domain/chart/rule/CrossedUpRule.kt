package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.indicator.Indicator
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

open class CrossedUpRule(private val upper: Indicator, private val lower: Indicator) : Rule {
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
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
        } while (result > 0 && upper[result] == lower[result])

        return result
    }
}