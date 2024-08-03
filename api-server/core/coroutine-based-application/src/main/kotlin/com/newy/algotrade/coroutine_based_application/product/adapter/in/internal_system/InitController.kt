package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.internal_system

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyProductQuery

class InitController(
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val userStrategyQuery: GetAllUserStrategyProductQuery,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategyKeys().forEach {
            runnableStrategyUseCase.setRunnableStrategy(it)
        }
    }
}