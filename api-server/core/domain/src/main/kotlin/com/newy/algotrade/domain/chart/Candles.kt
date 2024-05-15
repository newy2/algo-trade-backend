package com.newy.algotrade.domain.chart

interface Candles {
    val maxSize: Int
    val size: Int
    val firstIndex: Int
    val lastIndex: Int
    val firstCandle: Candle get() = get(firstIndex)
    val lastCandle: Candle get() = get(lastIndex)

    operator fun get(index: Int): Candle

    fun add(candle: Candle, isReplace: Boolean)

    fun upsert(candle: Candle) {
        validate(candle)
        add(candle, isReplace(candle))
    }

    fun upsert(candleList: List<Candle>) {
        val lastCandle = if (isEmpty()) candleList.first() else get(lastIndex)

        candleList
            .filter { it.time.begin >= lastCandle.time.begin }
            .forEach { upsert(it) }
    }

    private fun isEmpty(): Boolean = size == 0

    private fun validate(candle: Candle) {
        if (isEmpty()) {
            return
        }
        this[lastIndex].time.validate(nextTime = candle.time)
    }

    private fun isReplace(candle: Candle) =
        if (isEmpty()) {
            false
        } else {
            this[lastIndex].time == candle.time
        }
}