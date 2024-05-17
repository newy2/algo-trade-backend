package helpers

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.order.OrderSignalHistory

class BooleanArrayRule(
    vararg booleans: Boolean,
    private val values: List<Boolean> = booleans.toList()
) : Rule {
    override fun isSatisfied(index: Int, history: OrderSignalHistory?): Boolean = values[index]
}