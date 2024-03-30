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

    private fun isReplace(candle: Candle) =
        if (size == 0) {
            false
        } else {
            this[lastIndex].time == candle.time
        }

    private fun validate(candle: Candle) {
        if (size == 0) {
            return
        }

        this[lastIndex].time.checkNextTime(candle.time)
    }
}