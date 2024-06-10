package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery

class InitController(
    private val userStrategyController: SetUserStrategyController,
    private val userStrategyQuery: GetAllUserStrategyQuery,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategies().forEach {
            userStrategyController.setUserStrategy(it)
        }
    }
}