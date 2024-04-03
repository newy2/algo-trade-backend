package com.newy.algotrade.domain.library.ta4j.indicator

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class Ta4jEMAIndicator(barSeries: BarSeries, candleCount: Int) : Taj4NumIndicatorWrapper(
    EMAIndicator(ClosePriceIndicator(barSeries), candleCount)
)