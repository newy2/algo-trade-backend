package com.newy.algotrade.coroutine_based_application.product_price.adapter.`in`.internal_system

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.SetRunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyProductQuery

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