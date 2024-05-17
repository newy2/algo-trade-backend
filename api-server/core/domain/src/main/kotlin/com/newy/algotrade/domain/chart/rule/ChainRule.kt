package com.newy.algotrade.domain.chart.rule

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class ChainRule(
    private val initialRule: Rule,
    vararg c: ChainLink,
    private val chainLinks: List<ChainLink> = c.toList()
) : Rule {
    // TODO 나중에 리팩토링 하자. 현재는 Ta4j 의 구현코드로 복붙함
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean {
        var lastRuleWasSatisfiedAfterBars = 0
        var startIndex = index

        if (!initialRule.isSatisfied(index, history)) {
            return false
        }

        for (link in chainLinks) {
            var satisfiedWithinThreshold = false
            startIndex -= lastRuleWasSatisfiedAfterBars

            lastRuleWasSatisfiedAfterBars = 0
            for (i in 0..link.threshold) {
                val resultingIndex = startIndex - i
                if (resultingIndex < 0) {
                    break
                }
                satisfiedWithinThreshold = link.rule.isSatisfied(resultingIndex, history)
                if (satisfiedWithinThreshold) {
                    break
                }
                lastRuleWasSatisfiedAfterBars++
            }
            if (!satisfiedWithinThreshold) {
                return false
            }
        }

        return true
    }
}

class ChainLink(val rule: Rule, val threshold: Int)