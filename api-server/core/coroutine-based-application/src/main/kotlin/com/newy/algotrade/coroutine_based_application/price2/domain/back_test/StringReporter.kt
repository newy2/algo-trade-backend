package com.newy.algotrade.coroutine_based_application.price2.domain.back_test

import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import java.math.BigDecimal

class StringReporter(private val history: StrategySignalHistory) {
    fun report(): String {
        if (history.isEmpty()) {
            return ""
        }

        val result = StringBuilder()

        var entryCount = 0
        var exitCount = 0
        val revenues = mutableListOf<BigDecimal>()
        history.strategySignals().chunked(2).forEach {
            val entry = it[0]
            val exit = it.getOrNull(1)

            result.appendLine("ENTRY TIME: ${entry.timeFrame.begin}")
            result.appendLine("EXIT TIME: ${exit?.timeFrame?.begin ?: "-"}")
            result.appendLine("ENTRY PRICE: ${entry.price}")
            result.appendLine("EXIT PRICE: ${exit?.price ?: "-"}")
            result.appendLine("--------------------")

            entryCount++
            if (exit != null) {
                exitCount++
                revenues.add(
                    if (entry.orderType == OrderType.BUY)
                        exit.price - entry.price
                    else
                        entry.price - exit.price
                )
            }
        }

        val (totalRevenue, totalRevenueRate) = if (revenues.isNotEmpty()) {
            val revenue = revenues.sumOf { it }
            val revenueRate = revenue.setScale(2) / history.strategySignals().first().price * 100.toBigDecimal()
            revenue to "${revenueRate}%"
        } else {
            "-" to "-"
        }

        result.appendLine("TOTAL REVENUE: $totalRevenue")
        result.appendLine("TOTAL REVENUE RATE: $totalRevenueRate")
        result.append("TOTAL TRANSACTION COUNT: $exitCount (ENTRY: $entryCount, EXIT: $exitCount)")

        return result.toString()
    }
}