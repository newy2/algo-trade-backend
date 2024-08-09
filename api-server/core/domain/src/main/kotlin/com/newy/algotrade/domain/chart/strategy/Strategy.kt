package com.newy.algotrade.domain.chart.strategy

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.ChartFactory
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderType

open class Strategy(val entryType: OrderType, private val entryRule: Rule, private val exitRule: Rule) {
    companion object {
        fun fromClassName(strategyClassName: String, candles: Candles): Strategy {
            val packageName = "com.newy.algotrade.domain.chart.strategy.custom"
            val clazz = Class.forName("$packageName.$strategyClassName")
            val constructor = clazz.getConstructor(Candles::class.java, ChartFactory::class.java)

            return constructor.newInstance(candles, DEFAULT_CHART_FACTORY) as Strategy
        }
    }

    init {
        if (entryType == OrderType.NONE) {
            throw IllegalArgumentException("사용할 수 없는 OrderType 입니다")
        }
    }

    fun shouldOperate(index: Int, history: StrategySignalHistory): OrderType {
        validate(history)

        if (shouldEnter(index, history)) {
            return entryType
        }

        if (shouldExit(index, history)) {
            return entryType.completedType()
        }

        return OrderType.NONE
    }

    private fun validate(history: StrategySignalHistory) {
        // TODO 이거 수상하다. 나중에 필요 없으면 삭제해도 될듯.
        if (!arrayOf(OrderType.NONE, entryType).contains(history.firstOrderType())) {
            throw IllegalArgumentException("entryOrderType 이 다릅니다")
        }
    }

    private fun shouldEnter(index: Int, history: StrategySignalHistory): Boolean =
        entryRule.isSatisfied(index, history) && !history.isOpened()

    private fun shouldExit(index: Int, history: StrategySignalHistory): Boolean =
        exitRule.isSatisfied(index, history) && history.isOpened()
}
