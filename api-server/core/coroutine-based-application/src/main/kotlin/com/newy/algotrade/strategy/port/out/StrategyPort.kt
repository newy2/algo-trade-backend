package com.newy.algotrade.strategy.port.out

import com.newy.algotrade.domain.strategy.Strategy

interface StrategyPort :
    FindAllStrategiesPort,
    FindStrategyPort,
    ExistsStrategyPort

fun interface FindAllStrategiesPort {
    suspend fun findAllStrategies(): List<Strategy>
}

fun interface FindStrategyPort {
    suspend fun findStrategy(className: String): Strategy?
}

fun interface ExistsStrategyPort {
    suspend fun existsStrategy(className: String): Boolean
}