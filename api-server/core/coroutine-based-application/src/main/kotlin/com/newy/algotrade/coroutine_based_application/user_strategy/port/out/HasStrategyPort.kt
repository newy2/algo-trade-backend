package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

fun interface HasStrategyPort {
    suspend fun hasStrategyByClassName(strategyClassName: String): Boolean
}