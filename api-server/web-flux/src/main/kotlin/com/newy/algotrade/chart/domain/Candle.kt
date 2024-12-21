package com.newy.algotrade.chart.domain

import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class Candle private constructor(
    val time: TimeRange,
    val price: Price,
    val volume: BigDecimal,
) {
    enum class TimeFrame(val timePeriod: Duration) {
        M1(Duration.ofMinutes(1)),
        M3(Duration.ofMinutes(3)),
        M5(Duration.ofMinutes(5)),
        M15(Duration.ofMinutes(15)),
        M30(Duration.ofMinutes(30)),
        H1(Duration.ofHours(1)),
        D1(Duration.ofDays(1));

        operator fun invoke(
            beginTime: OffsetDateTime,
            openPrice: BigDecimal = BigDecimal.ZERO,
            highPrice: BigDecimal = BigDecimal.ZERO,
            lowPrice: BigDecimal = BigDecimal.ZERO,
            closePrice: BigDecimal = BigDecimal.ZERO,
            volume: BigDecimal = BigDecimal.ZERO,
        ) = Candle(
            TimeRange(this.timePeriod, beginTime),
            Price(
                openPrice,
                highPrice,
                lowPrice,
                closePrice,
            ),
            volume,
        )

        operator fun invoke(
            beginTime: Long,
            openPrice: BigDecimal = BigDecimal.ZERO,
            highPrice: BigDecimal = BigDecimal.ZERO,
            lowPrice: BigDecimal = BigDecimal.ZERO,
            closePrice: BigDecimal = BigDecimal.ZERO,
            volume: BigDecimal = BigDecimal.ZERO,
        ) = this.invoke(
            Instant.ofEpochMilli(beginTime).atOffset(ZoneOffset.UTC),
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
        val begin: OffsetDateTime,
        val end: OffsetDateTime = begin.plus(period)
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

    data class Price(
        val open: BigDecimal,
        val high: BigDecimal,
        val low: BigDecimal,
        val close: BigDecimal,
    ) {
        init {
            validate()
        }

        private fun validate() {
            arrayOf(low, open, close, high).also {
                it.sort()
            }.let {
                if (it.first() != low || it.last() != high) {
                    throw IllegalArgumentException("잘못된 price 입니다. low($low) =< open($open), close($close) =< high($high)")
                }
            }
        }
    }
}