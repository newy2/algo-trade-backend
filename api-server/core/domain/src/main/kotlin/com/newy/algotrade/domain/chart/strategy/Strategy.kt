package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType

open class Strategy(val entryType: OrderType, val entryRule: Rule, val exitRule: Rule) {
    init {
        if (entryType == OrderType.NONE) {
            throw IllegalArgumentException("사용할 수 없는 OrderType 입니다")
        }
    }

    fun shouldOperate(index: Int, history: OrderSignalHistory): OrderType {
        validate(history)

        val (shouldEnter, shouldExit) = compute(index, history)
        if (shouldEnter && history.lastOrderType() != entryType) {
            return entryType
        }

        if (shouldExit && history.lastOrderType() == entryType) {
            return entryType.completedType()
        }

        return OrderType.NONE
    }

    private fun validate(history: OrderSignalHistory) {
        // TODO 이거 수상하다. 나중에 필요 없으면 삭제해도 될듯.
        if (!arrayOf(OrderType.NONE, entryType).contains(history.firstOrderType())) {
            throw IllegalArgumentException("entryOrderType 이 다릅니다")
        }
    }

    private fun compute(index: Int, history: OrderSignalHistory): Pair<Boolean, Boolean> {
        // TODO Rule#isSatisfied 시간 측정
        val shouldEnter = entryRule.isSatisfied(index, history)
        val shouldExit = exitRule.isSatisfied(index, history)

        if (shouldEnter && shouldExit) {
            throw IllegalStateException("알고리즘 오류")
        }

        return Pair(shouldEnter, shouldExit)
    }
}
