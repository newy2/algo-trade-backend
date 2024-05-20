package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategy

enum class StrategyId(val id: String) {
    BuyTripleRSIStrategy("1");

    companion object {
        private val map = StrategyId.values().associateBy { it.id }
        infix fun from(id: String) = map.getValue(id)
    }
}

abstract class Strategy(private val entryType: OrderType, private val entryRule: Rule, private val exitRule: Rule) {
    companion object {
        fun create(id: StrategyId, candles: Candles): Strategy {
            return when (id) {
                StrategyId.BuyTripleRSIStrategy -> BuyTripleRSIStrategy(candles)
            }
        }
    }

    init {
        if (entryType == OrderType.NONE) {
            throw IllegalArgumentException("사용할 수 없는 OrderType 입니다")
        }
    }

    abstract fun version(): String

    fun shouldOperate(index: Int, history: OrderSignalHistory): OrderType {
        validate(history)

        if (shouldEnter(index, history)) {
            return entryType
        }

        if (shouldExit(index, history)) {
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

    private fun shouldEnter(index: Int, history: OrderSignalHistory): Boolean =
        entryRule.isSatisfied(index, history) && !history.isOpened()

    private fun shouldExit(index: Int, history: OrderSignalHistory): Boolean =
        exitRule.isSatisfied(index, history) && history.isOpened()
}
