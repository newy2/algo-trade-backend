package com.newy.algotrade.domain.chart.libs.ta4j

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import org.ta4j.core.BaseBarSeries

class Ta4jCandles(
    maxSize: Int = 400,
) : Candles, BaseBarSeries() {
    init {
        this.maximumBarCount = maxSize
    }

    override val maxSize
        get() = maximumBarCount

    override val size
        get() = barCount

    override val firstIndex
        get() = beginIndex

    override val lastIndex
        get() = endIndex

    override operator fun get(index: Int) =
        getBar(index).toCandle()

    override fun add(candle: Candle, isReplace: Boolean) =
        addBar(asBar(candle), isReplace)
}
