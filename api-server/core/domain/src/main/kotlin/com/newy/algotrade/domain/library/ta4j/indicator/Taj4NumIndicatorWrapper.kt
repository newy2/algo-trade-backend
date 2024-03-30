package com.newy.algotrade.domain.library.ta4j.indicator

import com.newy.algotrade.domain.chart.Indicator
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.Num
import java.math.BigDecimal
import org.ta4j.core.Indicator as _Ta4jIndicator

open class Taj4NumIndicatorWrapper(
    private val indicator: _Ta4jIndicator<Num>
) : Indicator {
    override operator fun get(index: Int): BigDecimal =
        indicator.getValue(index).let {
            (it as DecimalNum).delegate
        }
}