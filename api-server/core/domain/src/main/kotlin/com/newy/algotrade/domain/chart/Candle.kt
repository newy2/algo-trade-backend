package com.newy.algotrade.domain.chart

import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

data class TimeRange(
    val period: Duration,
    val begin: ZonedDateTime,
    val end: ZonedDateTime = begin.plus(period)
) {
    fun isSamePeriod(other: TimeRange) =
        this.period == other.period

    fun isOverlap(other: TimeRange) =
        begin.isBefore(other.begin) && end.isAfter(other.begin)
//        begin < other.begin && other.begin < end
}

data class Candle private constructor(
    val time: TimeRange,
    val openPrice: BigDecimal,
    val highPrice: BigDecimal,
    val lowPrice: BigDecimal,
    val closePrice: BigDecimal,
    val volume: BigDecimal,
) {
    init {
        if (highPrice < lowPrice) {
            throw IllegalArgumentException("highPrice 는 lowPrice 보다 작을 수 없습니다. highPrice($highPrice) < lowPrice($lowPrice)")
        }
        if (openPrice < lowPrice || highPrice < openPrice) {
            throw IllegalArgumentException("잘못된 openPrice 입니다. lowPrice($lowPrice) =< openPrice($openPrice) =< highPrice($highPrice)")
        }
        if (closePrice < lowPrice || highPrice < closePrice) {
            throw IllegalArgumentException("잘못된 highPrice 입니다. lowPrice($lowPrice) =< highPrice($highPrice) =< highPrice($highPrice)")
        }
    }

    enum class Factory(private val duration: Duration) {
        M1(Duration.ofMinutes(1)),
        M3(Duration.ofMinutes(3)),
        M5(Duration.ofMinutes(5)),
        M10(Duration.ofMinutes(10)),
        H1(Duration.ofHours(1)),
        D1(Duration.ofDays(1));

        operator fun invoke(
            beginTime: ZonedDateTime,
            openPrice: BigDecimal = BigDecimal.ZERO,
            highPrice: BigDecimal = BigDecimal.ZERO,
            lowPrice: BigDecimal = BigDecimal.ZERO,
            closePrice: BigDecimal = BigDecimal.ZERO,
            volume: BigDecimal = BigDecimal.ZERO,
        ) = Candle(
            TimeRange(this.duration, beginTime),
            openPrice,
            highPrice,
            lowPrice,
            closePrice,
            volume,
        )

        companion object {
            fun from(value: Duration) =
                Factory.values().find { it.duration == value }
        }
    }
}