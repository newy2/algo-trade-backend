package com.newy.algotrade.user_strategy.port.out

interface MarketPort
    : FindMarketPort

fun interface FindMarketPort {
    suspend fun findMarketIdsBy(marketAccountId: Long): List<Long>
}