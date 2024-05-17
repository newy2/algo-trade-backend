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
        override fun candles() = TA4J.candles()

        override fun adxIndicator(candles: Candles, candleCount: Int) =
            TA4J.adxIndicator(candles, candleCount)

        override fun rsiIndicator(candles: Candles, candleCount: Int) =
            TA4J.rsiIndicator(candles, candleCount)

        override fun emaIndicator(candles: Candles, candleCount: Int) =
            TA4J.emaIndicator(candles, candleCount)
    },
    TA4J {
        override fun candles(): Candles {
            return Ta4jCandles()
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

    abstract fun candles(): Candles
    abstract fun adxIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun rsiIndicator(candles: Candles, candleCount: Int): Indicator
    abstract fun emaIndicator(candles: Candles, candleCount: Int): Indicator
    fun openPriceIndicator(candles: Candles) = OpenPriceIndicator(candles)
    fun closePriceIndicator(candles: Candles) = ClosePriceIndicator(candles)
    fun constDecimalIndicator(constValue: Double): Indicator =
        ConstDecimalIndicator(BigDecimal.valueOf(constValue))
}
