package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

interface SetUserStrategyProductPort {
    suspend fun setUserStrategyProducts(
        userStrategyId: Long,
        productIds: List<Long>,
    ): Boolean
}