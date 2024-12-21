package com.newy.algotrade.chart.domain.strategy.custom

import com.newy.algotrade.chart.domain.Candles
import com.newy.algotrade.chart.domain.ChartFactory
import com.newy.algotrade.chart.domain.DEFAULT_CHART_FACTORY
import com.newy.algotrade.chart.domain.Rule
import com.newy.algotrade.chart.domain.indicator.ClosePriceIndicator
import com.newy.algotrade.chart.domain.rule.*
import com.newy.algotrade.chart.domain.indicator.Indicator
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.Strategy

class BuyTripleRSIStrategyV2(
    closePriceIndicator: ClosePriceIndicator,
    adx14Indicator: Indicator,
    ema50Indicator: Indicator,
    rsi7Indicator: Indicator,
    rsi14Indicator: Indicator,
    rsi21Indicator: Indicator,
    entryOrderType: OrderType,
    entryRule: Rule = AndRule(
        OverRule(closePriceIndicator, ema50Indicator),
        ChainRule(BooleanRule(true), ChainLink(OverRule(closePriceIndicator, ema50Indicator), 1)),
        OverRule(adx14Indicator, 20),
        OverRule(rsi7Indicator, 50),
        OverRule(rsi14Indicator, 50),
        OverRule(rsi21Indicator, 50),

        ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi7Indicator, 50), 10)),
        ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi14Indicator, 50), 10)),
        ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi14Indicator, 50), 10)),

        ChainRule(BooleanRule(true), ChainLink(CrossedUpRule(rsi7Indicator, rsi14Indicator), 10)),
        ChainRule(BooleanRule(true), ChainLink(CrossedUpRule(rsi14Indicator, rsi21Indicator), 10)),
    ),
    exitRule: Rule = OrRule(
        StopGainRule(closePriceIndicator, 2.5),
        StopLossRule(closePriceIndicator, 2.5),
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

