package com.newy.algotrade.domain.chart

class Strategy(val entryOrderType: OrderType, val entryRule: Rule, val exitRule: Rule) {
    fun shouldOperate(index: Int, history: OrderHistory): OrderType {
        validate(history)

        val (isEnter, isExit) = compute(index)
        if (isEnter && history.lastOrderType() != entryOrderType) {
            return entryOrderType
        }

        if (isExit && history.lastOrderType() == entryOrderType) {
            return entryOrderType.completedType()
        }

        return OrderType.NONE
    }

    private fun validate(history: OrderHistory) {
        // TODO 이거 수상하다. 나중에 필요 없으면 삭제해도 될듯.
        if (!arrayOf(OrderType.NONE, entryOrderType).contains(history.firstOrderType())) {
            throw IllegalArgumentException("entryOrderType 이 다릅니다")
        }
    }

    private fun compute(index: Int): Pair<Boolean, Boolean> {
        // TODO Rule#isSatisfied 시간 측정
        val isEnter = entryRule.isSatisfied(index)
        val isExit = exitRule.isSatisfied(index)

        if (isEnter && isExit) {
            throw IllegalStateException("알고리즘 오류")
        }

        return Pair(isEnter, isExit)
    }
}
