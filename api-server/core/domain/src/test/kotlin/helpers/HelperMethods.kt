package helpers

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import java.time.Duration
import java.time.OffsetDateTime

fun createOrderSignal(tradeType: OrderType) =
    StrategySignal(
        tradeType,
        Candle.TimeRange(Duration.ofMinutes(1), OffsetDateTime.now()),
        1000.0.toBigDecimal()
    )