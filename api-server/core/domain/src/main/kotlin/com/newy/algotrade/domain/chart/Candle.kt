package com.newy.algotrade.domain.chart

import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

data class Candle private constructor(
    val time: TimeRange,
    val openPrice: BigDecimal,
    val highPrice: BigDecimal,
    val lowPrice: BigDecimal,
    val closePrice: BigDecimal,
    val volume: BigDecimal,
) {
    init {
        validate()
    }

    private fun validate() {
        arrayOf(lowPrice, openPrice, closePrice, highPrice)
            .also {
                it.sort()
            }.let {
                if (it.first() != lowPrice || it.last() != highPrice) {
                    throw IllegalArgumentException("잘못된 price 입니다. lowPrice($lowPrice) =< openPrice($openPrice), closePrice($closePrice) =< highPrice($highPrice)")
                }
            }
    }

    enum class TimeFrame(private val timePeriod: Duration) {
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
            TimeRange(this.timePeriod, beginTime),
            openPrice,
            highPrice,
            lowPrice,
            closePrice,
            volume,
        )

        companion object {
            fun from(timePeriod: Duration) =
                TimeFrame.values().find { it.timePeriod == timePeriod }
        }
    }

    data class TimeRange(
        val period: Duration,
        val begin: ZonedDateTime,
        val end: ZonedDateTime = begin.plus(period)
    ) {
        private fun isSamePeriod(other: TimeRange) =
            period == other.period

        private fun isOverlap(other: TimeRange) =
            other.begin.let { otherBegin ->
                begin < otherBegin && otherBegin < end
            }

        fun validate(nextTime: TimeRange) {
            if (!isSamePeriod(nextTime)) {
                throw IllegalArgumentException("시간 간격이 다릅니다. [period(${period}) != nextTime#period(${nextTime.period})]")
            }
            if (isOverlap(nextTime)) {
                throw IllegalArgumentException("시간이 겹칩니다. (begin(${begin}) < nextTime#begin(${nextTime.begin}) < end(${end}))")
            }
        }
    }
}