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

    fun upsert(candleList: List<Candle>) {
        val lastCandle = get(lastIndex)

        candleList
            .filter { it.time.begin >= lastCandle.time.begin }
            .forEach { upsert(it) }
    }

    private fun validate(candle: Candle) {
        if (size == 0) {
            return
        }
        this[lastIndex].time.validate(nextTime = candle.time)
    }

    private fun isReplace(candle: Candle) =
        if (size == 0) {
            false
        } else {
            this[lastIndex].time == candle.time
        }
}