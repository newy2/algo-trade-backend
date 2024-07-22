package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system.InitController
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class Bootstrap(
    private val setRunnableStrategyController: SetRunnableStrategyController,
    private val getAllUserStrategyQuery: GetAllUserStrategyQuery,
    private val pollingProductPricePort: PollingProductPricePort,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            pollingProductPricePort.start()
        }
        CoroutineScope(Dispatchers.IO).launch {
            InitController(setRunnableStrategyController, getAllUserStrategyQuery).init()
        }
    }
}