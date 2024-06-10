package com.newy.algotrade.domain.chart.strategy

class TrafficLight(
    private val checkSignalPairSize: Int = 5
) {
    fun isGreen(strategySignalHistory: StrategySignalHistory): Boolean {
        val strategySignals = getStrategySignals(strategySignalHistory)
        if (strategySignals.isEmpty()) {
            return true
        }

        return checkGreenLight(getTradeResults(strategySignals))
    }

    private fun getStrategySignals(history: StrategySignalHistory): List<StrategySignal> {
        return (if (history.isOpened()) {
            history.strategySignals().dropLast(1)
        } else {
            history.strategySignals()
        }).takeLast(checkSignalPairSize * 2)
    }

    private fun getTradeResults(list: List<StrategySignal>): List<TradeResult> {
        return list
            .chunked(2)
            .map { (entrySignal, exitSignal) ->
                Trade(entrySignal, exitSignal).result()
            }.let {
                val remainSize = checkSignalPairSize - it.size
                it + List(remainSize) { TradeResult.WIN }
            }
    }

    private fun checkGreenLight(list: List<TradeResult>): Boolean {
        return list.filter { it == TradeResult.WIN }.size > (checkSignalPairSize / 2)
    }
}