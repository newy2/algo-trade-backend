package com.newy.algotrade.domain.chart.libs.ta4j.indicator

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.adx.ADXIndicator

class Ta4jADXIndicator(barSeries: BarSeries, candleCount: Int) : Taj4NumIndicatorWrapper(
    ADXIndicator(barSeries, candleCount)
)