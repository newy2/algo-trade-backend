package com.newy.algotrade.spring.auth.config

import com.newy.algotrade.spring.auth.resolver.AdminUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class ArgumentResolverConfig : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(AdminUserArgumentResolver())
        super.configureArgumentResolvers(configurer)
    }
}