package com.newy.algotrade.domain.chart

import com.newy.algotrade.domain.chart.indicator.ClosePriceIndicator
import com.newy.algotrade.domain.chart.indicator.ConstDecimalIndicator
import com.newy.algotrade.domain.chart.indicator.Indicator
import com.newy.algotrade.domain.chart.indicator.OpenPriceIndicator
import com.newy.algotrade.domain.chart.libs.ta4j.Ta4jCandles
import com.newy.algotrade.domain.chart.libs.ta4j.indicator.Ta4jADXIndicator
import com.newy.algotrade.domain.chart.libs.ta4j.indicator.Ta4jEMAIndicator
import com.newy.algotrade.domain.chart.libs.ta4j.indicator.Ta4jRSIIndicator
import java.math.BigDecimal

enum class ChartFactory {
    DEFAULT {
        override fun createCandles() = TA4J.createCandles()

        override fun createADXIndicator(candles: Candles, candleCount: Int) =
            TA4J.createADXIndicator(candles, candleCount)

        override fun createRSIIndicator(candles: Candles, candleCount: Int) =
            TA4J.createRSIIndicator(candles, candleCount)

        override fun createEMAIndicator(candles: Candles, candleCount: Int) =
            TA4J.createEMAIndicator(candles, candleCount)
    },
    TA4J {
        override fun createCandles(): Candles {
            return Ta4jCandles()
        }

        override fun createADXIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jADXIndicator(candles as Ta4jCandles, candleCount)
        }

        override fun createRSIIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jRSIIndicator(candles as Ta4jCandles, candleCount)
        }

        override fun createEMAIndicator(candles: Candles, candleCount: Int): Indicator {
            return Ta4jEMAIndicator(candles as Ta4jCandles, candleCount)
        }
    };

    abstract fun createCandles(): Candles
    abstract fun createADXIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun createRSIIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun createEMAIndicator(candles: Candles, candleCount: Int): Indicator
    fun openPriceIndicator(candles: Candles) = OpenPriceIndicator(candles)
    fun closePriceIndicator(candles: Candles) = ClosePriceIndicator(candles)
    fun createConstBigDecimalIndicator(constValue: Double): Indicator =
        ConstDecimalIndicator(BigDecimal.valueOf(constValue))
}
