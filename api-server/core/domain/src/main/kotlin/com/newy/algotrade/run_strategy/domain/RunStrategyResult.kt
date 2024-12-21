package com.newy.algotrade.run_strategy.domain

data class RunStrategyResult(
    var totalStrategyCount: Int = 0,
    var noneSignalCount: Int = 0,
    var buySignalCount: Int = 0,
    var sellSignalCount: Int = 0
)