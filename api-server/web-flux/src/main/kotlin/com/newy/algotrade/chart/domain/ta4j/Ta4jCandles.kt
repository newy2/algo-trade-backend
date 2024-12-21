package com.newy.algotrade.chart.domain.ta4j

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.DEFAULT_CANDLE_SIZE
import org.ta4j.core.BaseBarSeries

class Ta4jCandles(
    maxSize: Int = DEFAULT_CANDLE_SIZE,
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
