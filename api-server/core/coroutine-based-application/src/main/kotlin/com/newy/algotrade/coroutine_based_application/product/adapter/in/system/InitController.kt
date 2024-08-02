package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyQuery

class InitController(
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val userStrategyQuery: GetAllUserStrategyQuery,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategies().forEach {
            runnableStrategyUseCase.setRunnableStrategy(it)
        }
    }
}