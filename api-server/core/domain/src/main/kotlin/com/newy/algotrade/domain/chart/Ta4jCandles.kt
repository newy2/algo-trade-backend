package com.newy.algotrade.domain.chart

import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.num.DecimalNum

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

fun asBar(candle: Candle) = BaseBar(
    candle.time.period,
    candle.time.end,
    candle.openPrice,
    candle.highPrice,
    candle.lowPrice,
    candle.closePrice,
    candle.volume
)

fun Bar.toCandle() =
    (Candle.Factory.from(this.timePeriod)!!)(
        this.beginTime,
        (this.openPrice as DecimalNum).delegate,
        (this.highPrice as DecimalNum).delegate,
        (this.lowPrice as DecimalNum).delegate,
        (this.closePrice as DecimalNum).delegate,
        (this.volume as DecimalNum).delegate,
    )