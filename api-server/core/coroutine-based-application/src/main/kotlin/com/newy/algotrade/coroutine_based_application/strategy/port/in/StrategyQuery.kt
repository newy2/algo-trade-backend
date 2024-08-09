package com.newy.algotrade.coroutine_based_application.strategy.port.`in`

interface StrategyQuery :
    CheckRegisteredStrategyClassQuery,
    GetStrategyIdQuery,
    HasStrategyQuery

fun interface CheckRegisteredStrategyClassQuery {
    suspend fun checkRegisteredStrategyClasses()
}

fun interface GetStrategyIdQuery {
    suspend fun getStrategyId(className: String): Long
}

fun interface HasStrategyQuery {
    suspend fun hasStrategy(className: String): Boolean
}