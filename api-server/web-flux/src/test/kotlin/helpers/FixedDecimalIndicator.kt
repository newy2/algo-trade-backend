package helpers

import com.newy.algotrade.chart.domain.indicator.Indicator
import java.math.BigDecimal

class FixedDecimalIndicator(
    vararg numbers: Number,
    private val values: List<BigDecimal> = numbers.map { it.toDouble().toBigDecimal() }
) : Indicator {
    override fun get(index: Int): BigDecimal {
        return values[index]
    }
}