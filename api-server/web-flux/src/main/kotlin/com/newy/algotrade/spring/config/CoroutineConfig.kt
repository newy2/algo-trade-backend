package com.newy.algotrade.spring.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {
    @Bean
    fun backgroundScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}