package helpers

import com.newy.algotrade.domain.chart.Rule
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory

class BooleanRule(private val value: Boolean) : Rule {
    override fun isSatisfied(index: Int, history: StrategySignalHistory?): Boolean = value
}