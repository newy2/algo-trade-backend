package com.newy.algotrade.chart.domain

import com.newy.algotrade.chart.domain.indicator.Indicator
import com.newy.algotrade.chart.domain.ta4j.Ta4jCandles
import com.newy.algotrade.chart.domain.ta4j.indicator.Ta4jADXIndicator
import com.newy.algotrade.chart.domain.ta4j.indicator.Ta4jEMAIndicator
import com.newy.algotrade.chart.domain.ta4j.indicator.Ta4jRSIIndicator

val DEFAULT_CHART_FACTORY = ChartFactory.TA4J
const val DEFAULT_CANDLE_SIZE = 400

enum class ChartFactory {
    TA4J {
        override fun candles(candleSize: Int): Candles {
            return Ta4jCandles(candleSize)
        }

        override fun adxIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jADXIndicator(candles as Ta4jCandles, candleCount)
        }

        override fun rsiIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jRSIIndicator(candles as Ta4jCandles, candleCount)
        }

        override fun emaIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jEMAIndicator(candles as Ta4jCandles, candleCount)
        }
    };

    abstract fun candles(candleSize: Int = DEFAULT_CANDLE_SIZE): Candles
    abstract fun adxIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun rsiIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun emaIndicator(candles: Candles, candleCount: Int): Indicator
}
