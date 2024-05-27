package helpers

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class BooleanRule(private val value: Boolean) : Rule {
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean = value
}