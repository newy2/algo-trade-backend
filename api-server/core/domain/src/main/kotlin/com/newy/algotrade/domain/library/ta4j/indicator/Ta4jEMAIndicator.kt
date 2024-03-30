package com.newy.algotrade.domain.library.ta4j.indicator

import com.newy.algotrade.domain.chart.Candles
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class Ta4jEMAIndicator(candles: Candles, candleCount: Int) : Taj4NumIndicatorWrapper(
    EMAIndicator(ClosePriceIndicator(candles as BarSeries), candleCount)
)