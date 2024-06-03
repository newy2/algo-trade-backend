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
        history.orders().chunked(2).forEach {
            val entry = it[0]
            val exit = it.getOrNull(1)

            result.appendLine("ENTRY TIME: ${entry.timeFrame.begin}")
            result.appendLine("EXIT TIME: ${exit?.timeFrame?.begin ?: "-"}")
            result.appendLine("ENTRY PRICE: ${entry.orderPrice}")
            result.appendLine("EXIT PRICE: ${exit?.orderPrice ?: "-"}")
            result.appendLine("--------------------")

            entryCount++
            if (exit != null) {
                exitCount++
                revenues.add(
                    if (entry.type == OrderType.BUY)
                        exit.orderPrice - entry.orderPrice
                    else
                        entry.orderPrice - exit.orderPrice
                )
            }
        }

        val (totalRevenue, totalRevenueRate) = if (revenues.isNotEmpty()) {
            val revenue = revenues.sumOf { it }
            val revenueRate = revenue.setScale(2) / history.orders().first().orderPrice * 100.toBigDecimal()
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