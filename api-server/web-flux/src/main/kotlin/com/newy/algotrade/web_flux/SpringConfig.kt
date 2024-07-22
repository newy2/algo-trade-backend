package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.product.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfig {
    @Bean
    open fun OnCreatedStrategySignalPort(): OnCreatedStrategySignalPort {
        // TODO
        return object : OnCreatedStrategySignalPort {
            override suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal) {
                print("@@@userStrategyId: $userStrategyId, signal: $signal")
            }
        }
    }
}

