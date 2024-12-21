package helpers

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

class BooleanArrayRule(
    vararg booleans: Boolean,
    private val values: List<Boolean> = booleans.toList()
) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean = values[index]
}