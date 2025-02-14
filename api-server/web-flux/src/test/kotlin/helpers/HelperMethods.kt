package helpers

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime


fun createOrderSignal(tradeType: OrderType) =
    StrategySignal(
        tradeType,
        Candle.TimeRange(Duration.ofMinutes(1), OffsetDateTime.now()),
        1000.0.toBigDecimal()
    )

fun getSystemProperty(name: String): String =
    System.getProperty(name, System.getenv(name)) ?: ""

fun diffSeconds(from: LocalDateTime?, to: LocalDateTime?) = Duration.between(from, to).seconds