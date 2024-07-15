package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

interface HasStrategyPort {
    suspend fun hasStrategy(strategyId: Long): Boolean
}