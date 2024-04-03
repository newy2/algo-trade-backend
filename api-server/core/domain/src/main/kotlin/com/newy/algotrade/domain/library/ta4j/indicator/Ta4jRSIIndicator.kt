package com.newy.algotrade.domain.library.ta4j.indicator

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class Ta4jRSIIndicator(barSeries: BarSeries, candleCount: Int) : Taj4NumIndicatorWrapper(
    RSIIndicator(ClosePriceIndicator(barSeries), candleCount)
)
