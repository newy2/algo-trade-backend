package com.newy.algotrade.chart.domain.strategy.custom

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.ChartFactory
import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ClosePriceIndicator
import com.newy.algotrade.chart.domain.indicator.Indicator
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.rule.*
import com.newy.algotrade.chart.domain.strategy.Strategy

class BuyTripleRSIStrategy(
    closePriceIndicator: ClosePriceIndicator,
    adx14Indicator: Indicator,
    ema50Indicator: Indicator,
    rsi7Indicator: Indicator,
    rsi14Indicator: Indicator,
    rsi21Indicator: Indicator,
    entryOrderType: OrderType,
    entryRule: Rule = AndRule(
        OverRule(adx14Indicator, 20),
        OverRule(rsi7Indicator, 50),
        OverRule(rsi14Indicator, 50),
        OverRule(rsi21Indicator, 50),
        OverRule(closePriceIndicator, ema50Indicator),
        CrossedUpRule(rsi7Indicator, rsi14Indicator),
        CrossedUpRule(rsi14Indicator, rsi21Indicator),
    ),
    exitRule: Rule = OrRule(
        StopLossRule(closePriceIndicator, 2.5),
        StopGainRule(closePriceIndicator, 2.5),
    )
) : Strategy(entryOrderType, entryRule, exitRule) {
    constructor(candles: Candles, factory: ChartFactory = DEFAULT_CHART_FACTORY) : this(
        ClosePriceIndicator(candles),
        factory.adxIndicator(candles, 14),
        factory.emaIndicator(candles, 50),
        factory.rsiIndicator(candles, 7),
        factory.rsiIndicator(candles, 14),
        factory.rsiIndicator(candles, 21),
        OrderType.BUY
    )
}

