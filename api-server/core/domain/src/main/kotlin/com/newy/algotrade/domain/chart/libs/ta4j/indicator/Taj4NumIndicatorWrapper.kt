package com.newy.algotrade.domain.chart.libs.ta4j.indicator

import com.newy.algotrade.domain.chart.indicator.Indicator
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.Num
import java.math.BigDecimal
import org.ta4j.core.Indicator as _Ta4jIndicator

open class Taj4NumIndicatorWrapper(
    private val ta4jIndicator: _Ta4jIndicator<Num>
) : Indicator {
    override operator fun get(index: Int): BigDecimal =
        ta4jIndicator.getValue(index).let {
            (it as DecimalNum).delegate
        }
}