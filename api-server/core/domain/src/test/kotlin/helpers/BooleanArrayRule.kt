package helpers

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

class BooleanArrayRule(
    vararg booleans: Boolean,
    private val values: List<Boolean> = booleans.toList()
) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean = values[index]
}