package com.newy.algotrade.domain.rule

import com.newy.algotrade.domain.chart.Indicator
import com.newy.algotrade.domain.chart.Rule

open class CrossedUpRule(private val upper: Indicator, private val lower: Indicator) : Rule {
    override fun isSatisfied(index: Int): Boolean {
        return calculate(index)
    }

    private fun calculate(currentIndex: Int): Boolean {
        if (currentIndex == 0 || upper[currentIndex] <= lower[currentIndex]) {
            return false
        }

        val beforeIndex = findBeforeIndex(currentIndex)
        return upper[beforeIndex] < lower[beforeIndex]
    }

    private fun findBeforeIndex(index: Int): Int {
        var result = index

        do {
            result--
        } while (result > 0 && upper[result] == lower[result])

        return result
    }
}