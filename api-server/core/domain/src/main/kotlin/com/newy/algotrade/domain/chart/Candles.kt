package com.newy.algotrade.domain.chart

interface Candles {
    val maxSize: Int
    val size: Int
    val firstIndex: Int
    val lastIndex: Int

    operator fun get(index: Int): Candle

    fun add(candle: Candle, isReplace: Boolean)

    fun upsert(candle: Candle) {
        validate(candle)
        add(candle, isReplace(candle))
    }

    fun isReplace(candle: Candle) =
        if (size == 0) {
            false
        } else {
            this[lastIndex].time == candle.time
        }

    fun validate(candle: Candle) {
        if (size == 0) {
            return
        }

        this[lastIndex].time.let {
            if (!it.isSamePeriod(candle.time)) {
                throw IllegalArgumentException("시간 간격이 다릅니다. (Candles#period: ${it.period}, candle#period: ${candle.time.period})")
            }
            if (it.isOverlap(candle.time)) {
                throw IllegalArgumentException("시간이 겹칩니다. (lastBar#beginTime(${it.begin}) < candle#beginTime(${candle.time.begin}) < lastBar#endTime(${it.end}))")
            }
        }

    }
}