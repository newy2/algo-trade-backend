package com.newy.algotrade.domain.chart.indicator

import com.newy.algotrade.domain.chart.Candles

class OpenPriceIndicator(private val candles: Candles) : Indicator {
    override fun get(index: Int) = candles[index].price.open
}