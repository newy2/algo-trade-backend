package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase

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