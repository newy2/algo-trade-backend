package com.newy.algotrade.unit.price2.domain

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.FileBackTestingDataStore
import com.newy.algotrade.coroutine_based_application.price2.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.indicator.ClosePriceIndicator
import com.newy.algotrade.domain.chart.rule.*
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategyV2
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertTrue

@Deprecated("미사용 테스트")
class TripleRsiStrategyTest {
    @Test
    fun test() = runTest {
        val store = FileBackTestingDataStore(BackTestingFileManager())
        val backTestingDataKey = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            OffsetDateTime.parse("2024-06-01T00:00+09:00"),
            OffsetDateTime.parse("2024-06-05T00:00+09:00"),
        )

        val list = store.getBackTestingData(backTestingDataKey)
//        val candles: Candles = DEFAULT_CHART_FACTORY.candles(list.size)
        val candles: Candles = DEFAULT_CHART_FACTORY.candles(list.size)

        list.forEach {
            candles.upsert(it)
        }

        val element = list.find { it.time.begin.isEqual(OffsetDateTime.parse("2024-06-01T08:45+09:00")) }
        val index = list.indexOf(element)

        OffsetDateTime.parse("2024-06-01T08:45+09:00")
        val strategy = BuyTripleRSIStrategyV2(candles)

        val closePriceIndicator = ClosePriceIndicator(candles)
        val adx14Indicator = DEFAULT_CHART_FACTORY.adxIndicator(candles, 14)
        val ema50Indicator = DEFAULT_CHART_FACTORY.emaIndicator(candles, 50)
        val rsi7Indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 7)
        val rsi14Indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 14)
        val rsi21Indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 21)

        val rules = listOf(
            OverRule(closePriceIndicator, ema50Indicator),
            OverRule(adx14Indicator, 20),
            OverRule(rsi7Indicator, 50),
            OverRule(rsi14Indicator, 50),
            OverRule(rsi21Indicator, 50),
            ChainRule(BooleanRule(true), ChainLink(UnderRule(closePriceIndicator, ema50Indicator), 10)),
            ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi7Indicator, 50), 14)),
            ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi14Indicator, 50), 10)),
            ChainRule(BooleanRule(true), ChainLink(UnderRule(rsi14Indicator, 50), 10)),
            ChainRule(BooleanRule(true), ChainLink(CrossedUpRule(rsi7Indicator, rsi14Indicator), 16)),
            ChainRule(BooleanRule(true), ChainLink(CrossedUpRule(rsi14Indicator, rsi21Indicator), 16)),
        )
        val results = rules.map {
            it.isSatisfied(index, StrategySignalHistory())
        }

//        val underRule = UnderRule(ema50Indicator, 50)
//        println(underRule.isSatisfied(925))
//        println(underRule.isSatisfied(924))
//        println(underRule.isSatisfied(923))
//        println(underRule.isSatisfied(922))
//        println(underRule.isSatisfied(921))
//        println(underRule.isSatisfied(920))
//        println(underRule.isSatisfied(919))
//        println(underRule.isSatisfied(918))
//        println(underRule.isSatisfied(917))
//        println(underRule.isSatisfied(916))

        val result = strategy.shouldOperate(index, StrategySignalHistory())
        results.forEachIndexed { index, value ->
            assertTrue(value, "index: $index")
        }
    }
}