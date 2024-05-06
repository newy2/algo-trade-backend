package com.newy.algotrade.domain.chart.libs.ta4j

import com.newy.algotrade.domain.chart.Candle
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.num.DecimalNum

fun asBar(candle: Candle) = BaseBar(
    candle.time.period,
    candle.time.end,
    candle.price.open,
    candle.price.high,
    candle.price.low,
    candle.price.close,
    candle.volume
)

fun Bar.toCandle() =
    (Candle.TimeFrame.from(this.timePeriod)!!)(
        this.beginTime,
        (this.openPrice as DecimalNum).delegate,
        (this.highPrice as DecimalNum).delegate,
        (this.lowPrice as DecimalNum).delegate,
        (this.closePrice as DecimalNum).delegate,
        (this.volume as DecimalNum).delegate,
    )