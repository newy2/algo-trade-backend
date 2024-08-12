package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

interface MarketPort
    : FindMarketPort

fun interface FindMarketPort {
    suspend fun findMarketIdsBy(marketAccountId: Long): List<Long>
}