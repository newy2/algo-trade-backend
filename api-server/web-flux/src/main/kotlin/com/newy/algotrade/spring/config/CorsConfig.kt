package com.newy.algotrade.spring.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
open class CorsConfig(
    @Value("\${app.frontend.urls}") private val frontendUrls: String
) : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*frontendUrls.split(",").toTypedArray())
            .allowedMethods("*")
    }
}