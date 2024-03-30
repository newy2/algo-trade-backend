package com.newy.algotrade.domain.library.ta4j.indicator

import com.newy.algotrade.domain.chart.Candles
import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.adx.ADXIndicator

class Ta4jADXIndicator(candles: Candles, candleCount: Int) : Taj4NumIndicatorWrapper(
    ADXIndicator(candles as BarSeries, candleCount)
)