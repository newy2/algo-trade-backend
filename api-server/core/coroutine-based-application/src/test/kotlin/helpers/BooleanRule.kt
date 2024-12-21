package helpers

import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory

class BooleanRule(private val value: Boolean) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean = value
}