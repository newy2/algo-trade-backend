package com.newy.algotrade.product_price.adapter.`in`.internal_system

import com.newy.algotrade.run_strategy.port.`in`.SetRunnableStrategyUseCase
import com.newy.algotrade.user_strategy.port.`in`.GetAllUserStrategyProductQuery

class InitController(
    private val setRunnableStrategyUseCase: SetRunnableStrategyUseCase,
    private val getAllUserStrategyProductQuery: GetAllUserStrategyProductQuery,
) {
    suspend fun init() {
        getAllUserStrategyProductQuery.getAllUserStrategyKeys().forEach {
            setRunnableStrategyUseCase.setRunnableStrategy(it)
        }
    }
}