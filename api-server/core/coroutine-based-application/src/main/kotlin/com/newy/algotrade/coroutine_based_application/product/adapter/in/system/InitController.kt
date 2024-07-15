package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery

class InitController(
    private val runnableStrategyController: SetRunnableStrategyController,
    private val userStrategyQuery: GetAllUserStrategyQuery,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategies().forEach {
            runnableStrategyController.setUserStrategy(it)
        }
    }
}