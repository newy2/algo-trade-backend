package com.newy.algotrade.chart.domain.indicator

import com.newy.algotrade.chart.domain.Candles

class OpenPriceIndicator(private val candles: Candles) : Indicator {
    override fun get(index: Int) = candles[index].price.open
}